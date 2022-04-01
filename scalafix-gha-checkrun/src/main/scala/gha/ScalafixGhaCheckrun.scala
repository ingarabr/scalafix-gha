package gha

import cats.effect.{ExitCode, IO, IOApp}
import gha.st.actionsCore.mod.info
import gha.st.node.bufferMod.global.BufferEncoding
import gha.st.node.fsMod.PathLike
import gha.st.node.fsPromisesMod._
import io.circe.{DecodingFailure, Json}
import org.http4s.AuthScheme.Bearer
import org.http4s.Credentials.Token
import org.http4s.{MediaType, Method, Request, Uri}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.headers.{Accept, Authorization}
import org.http4s.circe._
import cats.syntax.all._
import gha.ActionInput.{Input, InputError}
import gha.st.actionsGithub.mod.context
import io.circe.parser.{parse => parseJson}

object ScalafixGhaCheckrun extends IOApp {

  private val clientResource = EmberClientBuilder.default[IO].build

  case class ScalafixActionInputs(
      annotationFile: PathLike,
      token: String
  )

  private val inputs =
    (
      Input[PathLike]("scalafix-annotation-file"),
      Input[String]("repo-token")
    )
      .mapN(ScalafixActionInputs.apply)
      .leftMap(InputError)
      .liftTo[IO]

  override def run(args: List[String]): IO[ExitCode] =
    clientResource.use(c => {
      for {
        inputs <- inputs
        _ = info("Staring action")
        jsonStr <- readFile(inputs.annotationFile, BufferEncoding.utf8)
          .liftTo[IO]
        annotations <- parseJson(jsonStr)
          .flatMap(
            _.asArray.toRight(DecodingFailure("expecting an array", List.empty))
          )
          .liftTo[IO]
        crUri <- GithHubUri.checkRun
        status = if (annotations.isEmpty) "success" else "failure"
        res <- c.expect[Json](
          Request[IO](method = Method.POST, uri = crUri)
            .putHeaders(
              Authorization(Token(Bearer, inputs.token)),
              Accept(new MediaType("application", "vnd.github.v3+json"))
            )
            .withEntity(
              Json.obj(
                "name" -> Json.fromString("Scalafix linting"),
                "head_sha" -> Json.fromString(context.sha),
                "status" -> Json.fromString("completed"),
                "conclusion" -> Json.fromString(status),
                "output" -> Json.obj(
                  "title" -> Json.fromString("Scalafix linting"),
                  "summary" -> Json.fromString("Linting violations..."),
                  "annotations" -> Json.fromValues(
                    annotations.take(50) // max 50 per request
                  )
                )
              )
            )
        )
        _ = info(s"""|CheckRun
              | - status: $status
              | - annotations: ${annotations.size} 
              |response:\n${res.spaces2}""".stripMargin)
      } yield ExitCode.Success
    })
}

object GithHubUri {
  val checkRun: IO[Uri] = Uri
    .fromString(
      s"https://api.github.com/repos/${context.repo.owner}/${context.repo.repo}/check-runs"
    )
    .liftTo[IO]
}

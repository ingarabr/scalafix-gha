package gha

import cats.data.{NonEmptyList, ValidatedNel}
import gha.st.node.fsMod.PathLike
import cats.syntax.all._
import gha.st.actionsCore.mod.getInput

object ActionInput {

  object Input {

    def safeInput(key: String): Either[String, String] =
      Either
        .catchNonFatal(getInput(key))
        .leftMap(err => s"Unable to read input $key: $err")

    trait InputType[A] {
      def resolve(str: String): Either[String, A]
    }

    implicit val stringInputType: InputType[String] = key =>
      safeInput(key)
        .flatMap(str => if (str.nonEmpty) Right(str) else Left("Empty string"))

    implicit val pathLikeInputType: InputType[PathLike] = key =>
      safeInput(key)
        .flatMap(path =>
          if (gha.st.node.fsMod.existsSync(path)) Right(path)
          else Left(s"[$key] File does not exists $path")
        )

    def apply[A](
        name: String
    )(implicit resolver: InputType[A]): ValidatedNel[String, A] =
      resolver.resolve(name).toValidatedNel

  }

  case class InputError(errors: NonEmptyList[String]) extends RuntimeException {
    final override def getMessage: String =
      errors.mkString_("[", ", ", "]")
  }

}

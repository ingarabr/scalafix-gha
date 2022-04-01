import io.circe.Json
import sbt.io.IO.{write => writeFile}
import sbt.{AutoPlugin, Def, File, Plugins, sbtOptionSyntaxRichOptional}
import scalafix.interfaces.ScalafixDiagnostic
import scalafix.sbt.{DiagnosticsWriter, ScalafixPlugin}

import java.nio.charset.StandardCharsets.UTF_8

object ScalafixGhaWriterPlugin extends AutoPlugin {
  override def requires: Plugins = ScalafixPlugin
  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      ScalafixPlugin.autoImport.scalafixDiagnosticsWriter := Some(
        GhaDiagnosticWriter
      )
    )
}

object GhaDiagnosticWriter extends DiagnosticsWriter {

  private val currentPath = new File("").getAbsolutePath + "/"

  override def write(
      targetFolder: sbt.File,
      diagnostic: Seq[ScalafixDiagnostic]
  ): Unit = {
    val annotations =
      diagnostic.flatMap(d =>
        d.position()
          .asScala
          .map(pos =>
            Json.obj(
              "path" -> Json
                .fromString(pos.input().filename().replaceAll(currentPath, "")),
              "start_line" -> Json.fromInt(pos.startLine()),
              "end_line" -> Json.fromInt(pos.endLine()),
              "start_column" -> Json.fromInt(pos.startColumn()),
              "end_column" -> Json.fromInt(pos.endColumn()),
              "annotation_level" -> Json.fromString("warning"),
              "message" -> Json
                .fromString(d.message ++ "\n" ++ d.explanation()),
              "title" -> Json.fromString(s"ScalaFix - ${d.severity()}"),
              "raw_details" -> Json
                .fromString(d.message ++ "\n" ++ d.explanation())
            )
          )
      )

    val jsonFileLocation = targetFolder.toPath.resolve("annotations.json")
    writeFile(jsonFileLocation.toFile, Json.arr(annotations: _*).spaces2, UTF_8)
    println(
      s"Wrote annotations to $jsonFileLocation. d: ${diagnostic.size} ${annotations.size}"
    )
  }

}

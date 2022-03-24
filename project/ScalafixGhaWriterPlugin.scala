import sbt.{AutoPlugin, Def, File, Plugins, sbtOptionSyntaxRichOptional}
import scalafix.interfaces.ScalafixDiagnostic
import scalafix.sbt.{DiagnosticsWriter, ScalafixPlugin}

object ScalafixGhaWriterPlugin extends AutoPlugin {
  override def requires = ScalafixPlugin
  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    ScalafixPlugin.autoImport.scalafixDiagnosticsWriter := Some(
      GhaDiagnosticWriter
    )
  )
}

object GhaDiagnosticWriter extends DiagnosticsWriter {

  private val currentPath = new File("").getAbsolutePath + "/"
  def toLine(d: ScalafixDiagnostic): Option[String] = {
    for {
      pos <- d.position().asScala
    } yield {
      val fields = List(
        ("file", pos.input().filename().replaceAll(currentPath, "")),
        ("title", s"ScalaFix - ${d.severity()}"),
        ("line", pos.startLine().toString),
        ("endLine", pos.endLine().toString),
        ("col", pos.startColumn().toString),
        ("endColumn", pos.endColumn().toString)
      ).map { case (k, v) => s"$k=$v" }.mkString(", ")
      "::warning " ++ fields ++ s"::${d.message().replaceAll("\n", "\\\\n")}"
    }
  }

  override def write(
      targetFolder: sbt.File,
      diagnostic: Seq[ScalafixDiagnostic]
  ): Unit =
    println(diagnostic.flatMap(toLine).mkString("\n"))
}

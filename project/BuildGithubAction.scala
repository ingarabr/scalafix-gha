import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.fullOptJS
import sbt.{AutoPlugin, Compile, Def, PluginTrigger, Plugins, taskKey}
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.webpack

object BuildGithubAction extends AutoPlugin {

  override val trigger: PluginTrigger = allRequirements
  override val requires: Plugins = ScalaJSBundlerPlugin && ScalaJSPlugin
  object autoImport {
    val buildGithubAction = taskKey[Unit]("Build action")
    val githubActionPath =
      taskKey[sbt.File]("Location the github action should be copied to")
  }

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    autoImport.buildGithubAction := {
      val copyTo =
        autoImport.githubActionPath.value.toPath.resolve("action.js").toFile
      val file: Seq[sbt.Attributed[sbt.File]] =
        (Compile / fullOptJS / webpack).value
      file
        .map(_.data)
        .filter(_.getName.endsWith(".js"))
        .toList match {
        case bundle :: Nil =>
          sbt.IO.copyFile(bundle, copyTo)
        case other => println(s"Ehm: $other")
      }
    }
  )
}

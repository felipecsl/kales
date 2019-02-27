package kales.cli.task

import kales.cli.Cli
import java.net.URL
import java.util.jar.Manifest

class KalesVersionTask : KalesTask {
  override fun run() {
    println(version())
  }

  companion object {
    fun version(): String {
      val clazz = Cli::class.java
      val classPath = clazz.getResource("${clazz.simpleName}.class").toString()
      return if (!classPath.startsWith("jar")) {
        // This will be used during unit tests
        System.getProperty("KALES_VERSION")
      } else {
        val manifestPath =
            classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF"
        val manifest = Manifest(URL(manifestPath).openStream())
        manifest.mainAttributes.getValue("Version")
      }
    }
  }
}

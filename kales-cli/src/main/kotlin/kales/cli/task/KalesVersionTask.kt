package kales.cli.task

import kales.cli.Cli
import java.net.URL
import java.util.jar.Manifest

class KalesVersionTask : KalesTask {
  override fun run() {
    println(kalesVersion())
  }

  companion object {
    fun kalesVersion(): String {
      val clazz = Cli::class.java
      val classPath = clazz.getResource("${clazz.simpleName}.class").toString()
      return if (!classPath.startsWith("jar")) {
        // This will be used during unit tests
        // TODO: This fallback is needed when running unit tests from IntelliJ, get rid of it
        System.getProperty("KALES_VERSION") ?: "0.0.6-SNAPSHOT"
      } else {
        val manifestPath =
            classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF"
        val manifest = Manifest(URL(manifestPath).openStream())
        manifest.mainAttributes.getValue("Version")
      }
    }
  }
}

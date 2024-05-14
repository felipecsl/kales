package kales.cli.task

import com.github.ajalt.clikt.core.UsageError
import kales.cli.copyToWithLogging
import kales.cli.relativePathFor
import kales.cli.task.KalesVersionTask.Companion.kalesVersion
import kales.cli.writeTextWithLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Files.exists
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermissions

/** "kales new" command: Creates a new Kales application */
class NewApplicationTask(
  currentDir: File,
  private val appName: String,
  private val kalesJarPath: String? = null
) : KalesTask {
  private val appRootDir = File(currentDir, appName)

  override fun run() {
    checkTargetDirectory()
    File(appRootDir, "build.gradle").writeTextWithLogging(buildFileContents())
    val srcDirRelativePath = (setOf("src", "main", "kotlin") + appName.split("."))
        .joinToString(File.separator)
    val sourcesDir = File(appRootDir, srcDirRelativePath)
    val appDir = File(sourcesDir, "app")
    appDir.mkdirs()
    File(sourcesDir, "Main.kt").writeTextWithLogging(mainAppFileContents())
    File(sourcesDir, "routes.kt").writeTextWithLogging(routesFileContents())
    setOf("controllers", "views", "models").forEach {
      File(appDir, it).mkdirs()
    }
    val layoutsDir = File(appDir, relativePathFor("views", "layouts"))
    layoutsDir.mkdirs()
    File(layoutsDir, "AppLayout.kt").writeTextWithLogging(appLayoutFileContents())
    File(sourcesDir, relativePathFor("db", "migrate")).mkdirs()
    val resourcesDir = File(appRootDir, relativePathFor("src", "main", "resources"))
    resourcesDir.mkdirs()
    File(resourcesDir, "database.yml").writeTextWithLogging("""
      development:
        adapter: sqlite
        host: localhost
        database: ${appName}_development
    """.trimIndent())
    val gradleWrapperDir = relativePathFor("gradle", "wrapper")
    File(appRootDir, gradleWrapperDir).mkdirs()
    File(File(appRootDir, gradleWrapperDir), "gradle-wrapper.properties")
        .writeTextWithLogging(GRADLE_WRAPPER_FILE_CONTENTS)
    copyResource("gradle-wrapper.bin", File(File(appRootDir, gradleWrapperDir), "gradle-wrapper.jar"))
    File(appRootDir, "gradlew").also { gradlewFile ->
      gradlewFile.toPath().makeExecutable()
      copyResource("gradlew", gradlewFile)
    }
    println("""

      New Kales (v${kalesVersion()}) project successfully initialized at '${appRootDir.absoluteFile.absolutePath}'.
      Happy coding!
      """.trimIndent())
  }

  private fun checkTargetDirectory() {
    if (!appRootDir.exists() && !appRootDir.mkdirs()) {
      throw UsageError("Failed to create directory ${appRootDir.absolutePath}")
    }
  }

  private fun copyResource(resourceName: String, destination: File) {
    val inputStream = javaClass.classLoader.getResourceAsStream(resourceName)
    // If the file is zero bytes we'll just consider it non-existing
    inputStream?.copyToWithLogging(destination)
        ?: throw RuntimeException("Failed to find resource $resourceName")
  }

  private fun Path.makeExecutable() {
    if (!exists(this)) {
      if (System.getProperty("os.name", "").toLowerCase().indexOf("win") >= 0) {
        Files.createFile(this)
      } else {
        val ownerWritable = PosixFilePermissions.fromString("rwxr--r--")
        val permissions = PosixFilePermissions.asFileAttribute(ownerWritable)
        Files.createFile(this, permissions)
      }
    }
  }

  private fun routesFileContents() = """
    package $appName

    import kales.KalesApplication
    import kales.actionview.ApplicationLayout

    fun <T : ApplicationLayout> routes(): KalesApplication<T>.() -> Unit = {
    }

  """.trimIndent()

  private fun appLayoutFileContents() = """
    package $appName.app.views.layouts

    import io.ktor.html.insert
    import kales.actionpack.KalesApplicationCall
    import kales.actionview.ApplicationLayout
    import kotlinx.html.*

    class AppLayout(call: KalesApplicationCall) : ApplicationLayout(call) {
      override fun HTML.apply() {
        head {
          title { +"Hello world" }
        }
        body {
          h1 { +"Hello World" }
          insert(body)
        }
      }
    }
  """.trimIndent()

  // TODO: For testing, we want to inject a different version of this build.gradle file so that we
  // can test it against a local kales jar, not the one from maven central.
  private fun buildFileContents() = """
    buildscript {
      repositories {
        jcenter()
      }
    }

    plugins {
      id 'application'
      id "org.jetbrains.kotlin.jvm" version "1.9.24"
    }

    repositories {
      jcenter()
      maven { url "http://oss.sonatype.org/content/repositories/snapshots" }
    }

    sourceSets {
      main.java.srcDirs += 'src/main/kotlin'
      test.java.srcDirs += 'src/test/kotlin'
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).all {
      kotlinOptions {
        jvmTarget = "1.8"
      }
    }

    mainClassName = '$appName.MainKt'

    dependencies { 
      implementation ${resolveKalesArtifactDependency()}
      implementation "io.ktor:ktor-server-netty:1.2.2"
    }
  """.trimIndent()

  private fun resolveKalesArtifactDependency(): String {
    // For testing inject the path to a local Kales fat jar
    return if (kalesJarPath != null) {
      "files('$kalesJarPath')"
    } else {
      "'com.felipecsl.kales:kales:${kalesVersion()}'"
    }
  }

  private fun mainAppFileContents() = """
      package $appName

      import io.ktor.application.Application
      import io.ktor.server.engine.embeddedServer
      import io.ktor.server.netty.Netty
      import kales.kalesApp
      import $appName.app.views.layouts.AppLayout

      fun Application.module() {
        kalesApp(AppLayout::class, routes())
      }

      fun main() {
        embeddedServer(
            Netty, 8080,
            watchPaths = listOf("."),
            module = Application::module
        ).start()
      }

      """.trimIndent()

  private val GRADLE_WRAPPER_FILE_CONTENTS = """
        #Wed Feb 13 09:15:40 PST 2019
        distributionBase=GRADLE_USER_HOME
        distributionPath=wrapper/dists
        zipStoreBase=GRADLE_USER_HOME
        zipStorePath=wrapper/dists
        distributionUrl=https\://services.gradle.org/distributions/gradle-5.0-all.zip
      """.trimIndent()
}

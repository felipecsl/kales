package kales.cli.task

import com.github.ajalt.clikt.core.UsageError
import kales.cli.copyToWithLogging
import kales.cli.relativePathFor
import kales.cli.writeTextWithLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Files.exists
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermissions

/** "kales new" command: Creates a new Kales application */
class NewCommandTask(
    private val appRootDir: File,
    private val appName: String
) : KalesTask {
  override fun run() {
    checkTargetDirectory()
    File(appRootDir, "build.gradle").writeTextWithLogging(buildFileContents())
    val srcDirRelativePath = (setOf("src", "main", "kotlin") + appName.split("."))
        .joinToString(File.separator)
    val sourcesDir = File(appRootDir, srcDirRelativePath)
    val appDir = File(sourcesDir, "app")
    appDir.mkdirs()
    setOf("controllers", "views", "models").forEach {
      File(appDir, it).mkdirs()
    }
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

      New Kales project successfully initialized at '${appRootDir.absoluteFile.absolutePath}'.
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
    inputStream.copyToWithLogging(destination)
  }

  private fun Path.makeExecutable() {
    if (!exists(this)) {
      val ownerWritable = PosixFilePermissions.fromString("rwxr--r--")
      val permissions = PosixFilePermissions.asFileAttribute(ownerWritable)
      Files.createFile(this, permissions)
    }
  }

  private fun buildFileContents() = """
    buildscript {
      repositories {
        jcenter()
      }
    }

    plugins {
      id 'application'
      id "org.jetbrains.kotlin.jvm" version "1.3.21"
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
      implementation "com.felipecsl.kales:kales:${KalesVersionTask.version()}"
    }
  """.trimIndent()

  companion object {
    private val GRADLE_WRAPPER_FILE_CONTENTS = """
        #Wed Feb 13 09:15:40 PST 2019
        distributionBase=GRADLE_USER_HOME
        distributionPath=wrapper/dists
        zipStoreBase=GRADLE_USER_HOME
        zipStorePath=wrapper/dists
        distributionUrl=https\://services.gradle.org/distributions/gradle-5.0-all.zip
      """.trimIndent()
  }
}
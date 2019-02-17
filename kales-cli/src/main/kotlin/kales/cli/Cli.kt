package kales.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermissions


class NewCommand : CliktCommand(name = "new", help = """
  The 'kales new' command creates a new Kales application with a default
    directory structure and configuration at the path you specify.
  """.trimIndent()) {
  private val appPath by option(help = """
    the path to your new app directory, eg.: ~/Code/Kotlin/weblog
  """.trimIndent()).required()
  private val appName by option(help = """
    the application name in reverse domain name notation, eg.: \"com.example.foo\""
  """.trimIndent()).required()

  override fun run() {
    val appDir = File(appPath)
    if (!appDir.exists() && !appDir.mkdirs()) {
      throw UsageError("Failed to create directory $appPath")
    }
    File(appDir, "build.gradle").writeText(buildFileContents())
    val srcDir = (listOf("src", "main", "kotlin") + appName.split(".")).joinToString(File.separator)
    File(appDir, srcDir).mkdirs()
    val gradleWrapperDir = listOf("gradle", "wrapper").joinToString(File.separator)
    File(appDir, gradleWrapperDir).mkdirs()
    File(File(appDir, gradleWrapperDir), "gradle-wrapper.properties")
        .writeText(GRADLE_WRAPPER_FILE_CONTENTS)
    copyResource("gradle-wrapper.bin", File(File(appDir, gradleWrapperDir), "gradle-wrapper.jar"))
    File(appDir, "gradlew").also { gradlewFile ->
      makeExecutable(gradlewFile.toPath())
      copyResource("gradlew", gradlewFile)
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
      implementation "com.felipecsl.kales:kales:0.0.1-SNAPSHOT"
    }
  """.trimIndent()

  private fun copyResource(resourceName: String, destination: File) {
    val classLoader = NewCommand::class.java.classLoader
    classLoader.getResourceAsStream(resourceName).use { input ->
      destination.outputStream().use { output ->
        input.copyTo(output)
      }
    }
  }

  private fun makeExecutable(file: Path) {
    val ownerWritable = PosixFilePermissions.fromString("rwxr--r--")
    val permissions = PosixFilePermissions.asFileAttribute(ownerWritable)
    Files.createFile(file, permissions)
  }

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

fun main(args: Array<String>) = NewCommand().main(args)
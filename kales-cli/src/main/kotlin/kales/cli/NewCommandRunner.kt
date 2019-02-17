package kales.cli

import com.github.ajalt.clikt.core.UsageError
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermissions

class NewCommandRunner(private val appPath: String, private val appName: String) {
  fun run() {
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

  private fun copyResource(resourceName: String, destination: File) {
    val classLoader = New::class.java.classLoader
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
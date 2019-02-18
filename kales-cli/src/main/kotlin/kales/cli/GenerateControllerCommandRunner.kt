package kales.cli

import com.github.ajalt.clikt.core.UsageError
import java.io.File

/** Generates a controller class */
class GenerateControllerCommandRunner(
    private val workingDirectory: File,
    private val name: String
) {
  fun run() {
    val appDirectory = findAppDirectory()
        ?: throw UsageError("Unable to find the `app` directory")
    val controllersDir = File(appDirectory, "controllers")
    val controllerName = when {
      name.endsWith("Controller.kt") -> name.replace(".kt", "")
      name.endsWith("Controller") -> name
      else -> "${name}Controller"
    }
    val packageName = determinePackageName(appDirectory)
    File(controllersDir, "$controllerName.kt").writeText("""
      package $packageName.app.controllers

      import io.ktor.application.ApplicationCall
      import kales.actionpack.ApplicationController

      class $controllerName(call: ApplicationCall) : ApplicationController(call) {
      }
    """.trimIndent())
    println(controllersDir.list())
  }

  /** Returns a File pointing to the application app/`type` directory or null if none found */
  private fun findAppDirectory(): File? {
    val kotlinDir = File(workingDirectory,
        listOf("src", "main", "kotlin").joinToString(File.separator))
    return kotlinDir.childDirectories()
        .mapNotNull { recursivelyFindAppDirectory(it) }
        .firstOrNull()
  }

  private fun determinePackageName(appDirectory: File): String? {
    return recursivelyDetermineAppPackageName(appDirectory, appDirectory)
  }

  private fun recursivelyDetermineAppPackageName(currentDir: File, appDir: File): String? {
    return when {
      currentDir.name == "src" ->
        null
      currentDir.parentFile.name == "kotlin" ->
        currentDir.toPath().relativize(appDir.toPath()).toFile().absolutePath
      else ->
        recursivelyDetermineAppPackageName(currentDir.parentFile, appDir)
    }
  }

  private fun recursivelyFindAppDirectory(currentDir: File): File? {
    val childDirs = currentDir.childDirectories()
    return if (currentDir.name == "app"
        && childDirs.map { it.name }.containsAll(setOf("controllers", "views", "models"))) {
      currentDir
    } else {
      childDirs.mapNotNull(this::recursivelyFindAppDirectory).firstOrNull()
    }
  }

  private fun File.childDirectories() =
      list().map { f -> File(this, f) }.filter { it.isDirectory }

}

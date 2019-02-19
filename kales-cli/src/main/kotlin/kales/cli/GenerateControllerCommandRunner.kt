package kales.cli

import com.github.ajalt.clikt.core.UsageError
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.ktor.application.ApplicationCall
import kales.actionpack.ApplicationController
import java.io.File

/** Generates a controller class */
class GenerateControllerCommandRunner(
    workingDirectory: File,
    private val name: String,
    private val actions: Set<String> = setOf()
) {
  private val kotlinDir =
      File(workingDirectory, listOf("src", "main", "kotlin").joinToString(File.separator))

  fun run() {
    val appDirectory = findKotlinDirectory()
        ?: throw UsageError("Unable to find the `app` sources directory")
    val controllerName = when {
      name.endsWith("Controller.kt") -> name.replace(".kt", "")
      name.endsWith("Controller") -> name
      else -> "${name}Controller"
    }.capitalize()
    val packageName = determinePackageName(appDirectory)
        ?: throw UsageError("""
          Unable to infer the package name for $controllerName.
          Make sure your sources directory structure follows the default
          "src/main/kotlin/your/package/name" structure
          """.trimIndent())
    writeControllerClassFile(kotlinDir, controllerName, packageName)
  }

  private fun writeControllerClassFile(
      controllersDir: File,
      controllerName: String,
      appPackageName: String
  ) {
    val controllerTypeSpec = TypeSpec.classBuilder(controllerName)
        .primaryConstructor(FunSpec.constructorBuilder()
            .addParameter("call", ApplicationCall::class)
            .build())
        .superclass(ApplicationController::class)
        .addSuperclassConstructorParameter("call")
        .addControllerActions()
        .build()
    val file = FileSpec.builder("$appPackageName.app.controllers", controllerName)
        .addType(controllerTypeSpec)
        .build()
    file.writeTo(controllersDir)
  }

  private fun TypeSpec.Builder.addControllerActions(): TypeSpec.Builder {
    actions.forEach { addControllerAction(it) }
    return this
  }

  private fun TypeSpec.Builder.addControllerAction(actionName: String): TypeSpec.Builder {
    return addFunction(
        FunSpec.builder(actionName)
            .returns(Any::class.asTypeName().copy(nullable = true))
            .addStatement("return null")
            .build())
  }

  /** Returns a File pointing to the application app/`type` directory or null if none found */
  private fun findKotlinDirectory(): File? {
    return kotlinDir.childDirectories()
        .mapNotNull(this::recursivelyFindAppDirectory)
        .firstOrNull()
  }

  private fun determinePackageName(appDirectory: File): String? {
    return recursivelyDetermineAppPackageName(appDirectory, appDirectory)
        ?.split("/")
        ?.joinToString(".")
  }

  private fun recursivelyDetermineAppPackageName(currentDir: File, appDir: File): String? {
    return when {
      currentDir.name == "src" ->
        null
      currentDir.name == "kotlin" ->
        currentDir.toPath().relativize(appDir.parentFile.toPath()).toString()
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
      safeListFiles().filter { it.isDirectory }
}

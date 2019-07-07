package kales.cli.task

import com.github.ajalt.clikt.core.UsageError
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.ktor.application.ApplicationCall
import kales.actionpack.ApplicationController
import java.io.File

/** Generates a controller class */
class GenerateControllerTask(
  workingDirectory: File,
  private val name: String,
  private val actions: Set<String> = setOf()
) : KalesContextualTask(workingDirectory) {
  override fun run() {
    val controllerName = when {
      name.endsWith("Controller.kt") -> name.replace(".kt", "")
      name.endsWith("Controller") -> name
      else -> "${name}Controller"
    }.capitalize()
    if (appPackageName == null) {
      throw UsageError("""
          Unable to infer the package name for $controllerName.
          Make sure your sources directory structure follows the default
          "src/main/kotlin/your/package/name" structure
          """.trimIndent())
    }
    val controllersDir = File(appDirectory, "controllers")
    writeControllerClassFile(controllersDir, controllerName)
  }

  private fun writeControllerClassFile(
    controllersDir: File,
    controllerName: String
  ) {
    val file = buildFileSpec(controllerName)
    val outputPath = controllersDir.toPath().resolve("$controllerName.kt")
    file.rawWriteTo(outputPath)
  }

  private fun buildFileSpec(controllerName: String): FileSpec {
    val controllerTypeSpec = TypeSpec.classBuilder(controllerName)
      .primaryConstructor(FunSpec.constructorBuilder()
        .addParameter("call", ApplicationCall::class)
        .build())
      .superclass(ApplicationController::class)
      .addSuperclassConstructorParameter("call")
      .addControllerActions()
      .build()
    return FileSpec.builder("$appPackageName.app.controllers", controllerName)
      .addType(controllerTypeSpec)
      .build()
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
}

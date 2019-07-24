package kales.cli.task

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kales.actionview.ActionView
import kales.actionview.ViewModel
import kotlinx.html.FlowContent
import java.io.File

class GenerateViewTask(
  workingDir: File,
  private val resourceName: String,
  private val viewName: String,
  private val viewModelName: String = "${viewName}Model"
) : KalesContextualTask(workingDir) {
  override fun run() {
    val resourceViewsDir = File(File(appDirectory, "views"), resourceName)
    writeViewClassFile(resourceViewsDir)
  }

  private fun writeViewClassFile(viewsDir: File) {
    val files = buildFileSpecs()
    val outputPath = viewsDir.toPath().resolve("$viewName.kt")
    files.forEach { it.rawWriteTo(outputPath) }
  }

  private fun buildFileSpecs(): Set<FileSpec> {
    val viewPackageName = "$appPackageName.views.$resourceName"
    val viewModelClassName = ClassName(viewPackageName, viewModelName)
    val viewModelTypeSpec = TypeSpec.classBuilder(viewModelName)
      .superclass(ViewModel::class)
      .addModifiers(KModifier.DATA)
      .build()
    val viewModelFileSpec = FileSpec.builder(viewPackageName, viewModelName)
      .addType(viewModelTypeSpec)
      .build()
    val superclass = ActionView::class.asTypeName()
      .parameterizedBy(viewModelClassName)
    val viewTypeSpec = TypeSpec.classBuilder(viewName)
      .superclass(superclass)
      .addSuperclassConstructorParameter("bindings")
      .primaryConstructor(FunSpec.constructorBuilder()
        .addParameter("bindings", viewModelClassName.copy(nullable = true))
        .build())
      .addFunction(FunSpec.builder("render")
        .addModifiers(KModifier.OVERRIDE)
        .receiver(FlowContent::class)
        .build())
      .build()
    val viewFileSpec = FileSpec.builder(viewPackageName, viewName)
      .addType(viewTypeSpec)
      .build()
    return setOf(viewModelFileSpec, viewFileSpec)
  }
}
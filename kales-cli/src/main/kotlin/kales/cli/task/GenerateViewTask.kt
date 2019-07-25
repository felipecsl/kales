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
  private val viewPackageName = "$appPackageName.app.views.$resourceName"

  override fun run() {
    val resourceViewsDir = File(File(appDirectory, "views"), resourceName)
    resourceViewsDir.mkdirs()
    writeViewClassFile(resourceViewsDir)
  }

  private fun writeViewClassFile(viewsDir: File) {
    val viewFile = buildViewFileSpec()
    val viewModelFile = buildViewModelFileSpec()
    val viewOutputPath = viewsDir.toPath().resolve("$viewName.kt")
    val viewModelOutputPath = viewsDir.toPath().resolve("${viewName}Model.kt")
    viewFile.rawWriteTo(viewOutputPath)
    viewModelFile.rawWriteTo(viewModelOutputPath)
  }

  private fun buildViewFileSpec(): FileSpec {
    val viewModelClassName = ClassName(viewPackageName, viewModelName)
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
    return FileSpec.builder(viewPackageName, viewName)
      .addType(viewTypeSpec)
      .build()
  }

  private fun buildViewModelFileSpec(): FileSpec {
    val viewModelTypeSpec = TypeSpec.classBuilder(viewModelName)
      .addSuperinterface(ViewModel::class)
      .build()
    return FileSpec.builder(viewPackageName, viewModelName)
      .addType(viewModelTypeSpec)
      .build()
  }
}
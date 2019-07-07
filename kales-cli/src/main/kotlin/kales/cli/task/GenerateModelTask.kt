package kales.cli.task

import com.squareup.kotlinpoet.*
import kales.ApplicationRecord
import kales.activemodel.MaybeRecordId
import java.io.File
import java.util.*

class GenerateModelTask(
  private val workingDir: File,
  private val modelName: String,
  private val dateProvider: () -> Date = { Date() }
) : KalesContextualTask(workingDir) {
  override fun run() {
    val modelsDir = File(appDirectory, "models")
    writeModelClassFile(modelsDir)
    val tableName = if (modelName.endsWith("s")) {
      modelName
    } else {
      "${modelName}s"
    }.toLowerCase()
    val migrationName = "Create${modelName.capitalize()}"
    val migrationTask = GenerateMigrationTask(workingDir, migrationName, tableName, dateProvider)
    migrationTask.run()
  }

  private fun writeModelClassFile(modelsDir: File) {
    val file = buildFileSpec(modelName)
    val outputPath = modelsDir.toPath().resolve("$modelName.kt")
    file.rawWriteTo(outputPath)
  }

  private fun buildFileSpec(modelName: String): FileSpec {
    val modelClass = ClassName.bestGuess(modelName)
    val companion = TypeSpec.companionObjectBuilder()
      .addFunction(FunSpec.builder("all")
        .addStatement("return allRecords<%T>()", modelClass)
        .build())
      .addFunction(FunSpec.builder("find")
        .addParameter("id", Int::class)
        .addStatement("return findRecord<%T>(id)", modelClass)
        .build())
      .build()
    val modelTypeSpec = TypeSpec.classBuilder(modelName)
      .primaryConstructor(FunSpec.constructorBuilder()
        .addParameter("id", MaybeRecordId::class)
        .build())
      .addProperty(PropertySpec.builder("id", MaybeRecordId::class)
        .initializer("id")
        .addModifiers(KModifier.OVERRIDE)
        .build())
      .addSuperinterface(ApplicationRecord::class)
      .addModifiers(KModifier.DATA)
      .addType(companion)
      .build()
    return FileSpec.builder("$appPackageName.app.models", modelName)
      .addType(modelTypeSpec)
      .addImport(ApplicationRecord.Companion::class, "findRecord", "allRecords")
      .build()
  }
}
package kales.cli.task

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import kales.migrations.Migration
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GenerateMigrationTask(
    workingDir: File,
    private val migrationClassName: String,
    /** Optionally add a create/drop table statement pair to the new migration */
    private val tableToCreate: String? = null,
    private val dateProvider: () -> Date = { Date() }
) : KalesContextualTask(workingDir) {
  override fun run() {
    val timestamp = SimpleDateFormat("yyyyMMddhhmmss").format(dateProvider())
    val upFunSpecBuilder = FunSpec.builder("up")
        .addModifiers(KModifier.OVERRIDE)
    val downFunSpecBuilder = FunSpec.builder("down")
        .addModifiers(KModifier.OVERRIDE)
    if (tableToCreate != null) {
      upFunSpecBuilder.addStatement("createTable(\"$tableToCreate\") {}")
      downFunSpecBuilder.addStatement("dropTable(\"$tableToCreate\")")
    }
    val migrationTypeSpec = TypeSpec.classBuilder(migrationClassName)
        .superclass(Migration::class)
        .addFunction(upFunSpecBuilder.build())
        .addFunction(downFunSpecBuilder.build())
        .build()
    val fileSpec = FileSpec.builder("$appPackageName.db.migrate", migrationClassName)
        .addType(migrationTypeSpec)
        .build()
    val outputPath = dbMigrateDir.toPath().resolve("M${timestamp}_$migrationClassName.kt")
    fileSpec.rawWriteTo(outputPath)
  }
}
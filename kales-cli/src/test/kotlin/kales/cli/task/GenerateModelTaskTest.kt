package kales.cli.task

import org.junit.Test

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GenerateModelTaskTest {
  @get:Rule val tempDir = TemporaryFolder()
  private val kalesFatJarPath = System.getProperty("KALES_FAT_JAR_PATH")

  @Test fun `test create model`() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    val date = Date()
    val timestamp = SimpleDateFormat("yyyyMMddhhmmss").format(date)
    NewApplicationTask(root, appName, kalesFatJarPath).run()
    val appDir = File(root, appName)
    GenerateModelTask(appDir, "Bar") { date }.run()
    val dbMigrateDir = File(appDir, "src/main/kotlin/com/example/testapp/db/migrate")
    val migrationFile = File(dbMigrateDir, "M${timestamp}_CreateBar.kt")
    assertThat(dbMigrateDir.listFiles()!!.toList()).containsExactly(migrationFile)
    assertThat(migrationFile.readText()).isEqualTo("""
      package com.example.testapp.db.migrate

      import kales.migrations.Migration

      class CreateBar : Migration() {
        override fun up() {
          createTable("bars") {}
        }

        override fun down() {
          dropTable("bars")
        }
      }

    """.trimIndent())
    val modelFile = File(appDir, "src/main/kotlin/com/example/testapp/app/models/Bar.kt")
    assertThat(modelFile.exists()).isTrue()
    assertThat(modelFile.readText()).isEqualTo("""
      package com.example.testapp.app.models

      import kales.ApplicationRecord
      import kales.ApplicationRecord.Companion.allRecords
      import kales.ApplicationRecord.Companion.findRecord
      import kales.activemodel.MaybeRecordId
      import kotlin.Int

      data class Bar(
        override val id: MaybeRecordId
      ) : ApplicationRecord {
        companion object {
          fun all() = allRecords<Bar>()

          fun find(id: Int) = findRecord<Bar>(id)
        }
      }

    """.trimIndent())
  }

  @Test fun `project with new model builds correctly`() {
    val root = tempDir.root
    val appName = "com.example.testapp2"
    val date = Date()
    NewApplicationTask(root, appName, kalesFatJarPath).run()
    val appDir = File(root, appName)
    GenerateModelTask(appDir, "Bar") { date }.run()
    val result = GradleRunner.create()
        .withProjectDir(appDir)
        .withArguments("jar")
        .build()
    assertThat(result.task(":jar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
  }
}
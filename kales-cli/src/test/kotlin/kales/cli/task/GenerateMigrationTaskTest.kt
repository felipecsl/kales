package kales.cli.task

import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GenerateMigrationTaskTest {
  @get:Rule val tempDir = TemporaryFolder()

  @Test fun `test create migration`() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    val date = Date()
    val timestamp = SimpleDateFormat("yyyyMMddhhmmss").format(date)
    NewApplicationTask(root, appName).run()
    val appDir = File(root, appName)
    GenerateMigrationTask(appDir, "CreateFooBar") { date }.run()
    val dbMigrateDir = File(appDir, "src/main/kotlin/com/example/testapp/db/migrate")
    val migrationFile = File(dbMigrateDir, "M${timestamp}_CreateFooBar.kt")
    assertThat(dbMigrateDir.listFiles().toList()).containsExactly(migrationFile)
    assertThat(migrationFile.readText()).isEqualTo("""
      package com.example.testapp.db.migrate

      import kales.migrations.Migration

      class CreateFooBar : Migration() {
          override fun up() {
          }

          override fun down() {
          }
      }

    """.trimIndent())
  }

  @Test fun `create migration with create table`() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    val date = Date()
    val timestamp = SimpleDateFormat("yyyyMMddhhmmss").format(date)
    NewApplicationTask(root, appName).run()
    val appDir = File(root, appName)
    GenerateMigrationTask(appDir, "CreateFooBar", "haha") { date }.run()
    val dbMigrateDir = File(appDir, "src/main/kotlin/com/example/testapp/db/migrate")
    val migrationFile = File(dbMigrateDir, "M${timestamp}_CreateFooBar.kt")
    assertThat(dbMigrateDir.listFiles().toList()).containsExactly(migrationFile)
    assertThat(migrationFile.readText()).isEqualTo("""
      package com.example.testapp.db.migrate

      import kales.migrations.Migration

      class CreateFooBar : Migration() {
          override fun up() {
              createTable("haha") {}
          }

          override fun down() {
              dropTable("haha")
          }
      }

    """.trimIndent())
  }
}
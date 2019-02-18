package kales.cli

import com.github.ajalt.clikt.core.UsageError
import com.google.common.truth.Truth.assertThat
import org.junit.Test

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class GenerateControllerCommandRunnerTest {
  @get:Rule val tempDir = TemporaryFolder()

  @Test fun `test correctly creates an empty controller class`() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    NewCommandRunner(root, appName).run()
    GenerateControllerCommandRunner(root, "bar").run()
    val controllerFile = File(root,
        "src/main/kotlin/com/example/testapp/app/controllers/BarController.kt")
    assertThat(controllerFile.exists()).isTrue()
    assertThat(controllerFile.readText()).isEqualTo("""
      package com.example.testapp.app.controllers

      import io.ktor.application.ApplicationCall
      import kales.actionpack.ApplicationController

      class BarController(call: ApplicationCall) : ApplicationController(call) {
      }
    """.trimIndent())
  }

  @Test(expected = UsageError::class)
  fun `invalid or inexistent app directory`() {
    GenerateControllerCommandRunner(File("."), "bar").run()
  }
}
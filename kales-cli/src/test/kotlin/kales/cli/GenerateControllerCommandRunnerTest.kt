package kales.cli

import com.github.ajalt.clikt.core.UsageError
import com.google.common.truth.Truth.assertThat
import org.junit.Test

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class GenerateControllerCommandRunnerTest {
  @get:Rule val tempDir = TemporaryFolder()

  @Test fun `test generate an empty controller class`() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    NewCommandRunner(root, appName).run()
    GenerateControllerCommandRunner(root, "bar").run()
    println(File(root,
        "src/main/kotlin/com/example/testapp/app/controllers").safeListFiles())
    val controllerFile = File(root,
        "src/main/kotlin/com/example/testapp/app/controllers/BarController.kt")
    assertThat(controllerFile.exists()).isTrue()
    assertThat(controllerFile.readText()).isEqualTo("""
      package com.example.testapp.app.controllers

      import io.ktor.application.ApplicationCall
      import kales.actionpack.ApplicationController

      class BarController(call: ApplicationCall) : ApplicationController(call)

    """.trimIndent())
  }

  @Test fun `test generate a controller with actions`() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    NewCommandRunner(root, appName).run()
    GenerateControllerCommandRunner(root, "bar", setOf("blah", "foo")).run()
    val controllerFile = File(root,
        "src/main/kotlin/com/example/testapp/app/controllers/BarController.kt")
    assertThat(controllerFile.exists()).isTrue()
    assertThat(controllerFile.readText()).isEqualTo("""
      package com.example.testapp.app.controllers

      import io.ktor.application.ApplicationCall
      import kales.actionpack.ApplicationController
      import kotlin.Any

      class BarController(call: ApplicationCall) : ApplicationController(call) {
          fun blah(): Any? = null

          fun foo(): Any? = null
      }

    """.trimIndent())
  }

  @Test(expected = UsageError::class)
  fun `invalid or inexistent app directory`() {
    GenerateControllerCommandRunner(File("."), "bar").run()
  }
}
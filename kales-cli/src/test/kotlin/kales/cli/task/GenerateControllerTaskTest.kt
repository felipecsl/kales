package kales.cli.task

import com.github.ajalt.clikt.core.UsageError
import com.google.common.truth.Truth.assertThat
import org.junit.Test

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class GenerateControllerTaskTest {
  @get:Rule val tempDir = TemporaryFolder()

  @Test fun `test generate an empty controller class`() {
    val appName = "com.example.testapp"
    val root = File(tempDir.root, appName)
    NewApplicationTask(tempDir.root, appName).run()
    GenerateControllerTask(root, "bar").run()
    val controllerFile = File(root,
        "src/main/kotlin/com/example/testapp/app/controllers/BarController.kt")
    assertThat(controllerFile.exists()).isTrue()
    assertThat(controllerFile.readText()).isEqualTo("""
      package com.example.testapp.app.controllers

      import kales.actionpack.ApplicationController
      import kales.actionpack.KalesApplicationCall

      class BarController(
        call: KalesApplicationCall
      ) : ApplicationController(call)

    """.trimIndent())
  }

  @Test fun `test generate a controller with actions`() {
    val appName = "com.example.testapp"
    val root = File(tempDir.root, appName)
    NewApplicationTask(root, appName).run()
    val appDir = File(root, appName)
    GenerateControllerTask(appDir, "bar", setOf("blah", "foo")).run()
    val controllerFile = File(appDir,
        "src/main/kotlin/com/example/testapp/app/controllers/BarController.kt")
    assertThat(controllerFile.exists()).isTrue()
    assertThat(controllerFile.readText()).isEqualTo("""
      package com.example.testapp.app.controllers

      import kales.actionpack.ApplicationController
      import kales.actionpack.KalesApplicationCall
      import kotlin.Any

      class BarController(
        call: KalesApplicationCall
      ) : ApplicationController(call) {
        fun blah(): Any? = null

        fun foo(): Any? = null
      }

    """.trimIndent())
  }

  @Test(expected = UsageError::class)
  fun `invalid or inexistent app directory`() {
    GenerateControllerTask(File("."), "bar").run()
  }
}
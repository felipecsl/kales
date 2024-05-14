package kales.cli.task

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import kotlin.test.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class GenerateViewTaskTest {
  @get:Rule val tempDir = TemporaryFolder()
  private val kalesFatJarPath = System.getProperty("KALES_FAT_JAR_PATH")

  @Test fun `test generate a view`() {
    val appName = "com.example.testapp"
    val root = File(tempDir.root, appName)
    NewApplicationTask(tempDir.root, appName, kalesFatJarPath).run()
    GenerateViewTask(root, "bar", "IndexView").run()
    val viewFile = File(root, "src/main/kotlin/com/example/testapp/app/views/bar/IndexView.kt")
    assertThat(viewFile.exists()).isTrue()
    assertThat(viewFile.readText()).isEqualTo("""
      package com.example.testapp.app.views.bar

      import kales.actionpack.KalesApplicationCall
      import kales.actionview.ActionView
      import kotlinx.html.FlowContent

      class IndexView(
        call: KalesApplicationCall,
        bindings: IndexViewModel?
      ) : ActionView<IndexViewModel>(call, bindings) {
        override fun FlowContent.render() {
        }
      }

    """.trimIndent())
    val viewModelFile = File(root,
      "src/main/kotlin/com/example/testapp/app/views/bar/IndexViewModel.kt")
    assertThat(viewModelFile.exists()).isTrue()
    assertThat(viewModelFile.readText()).isEqualTo("""
      package com.example.testapp.app.views.bar

      import kales.actionpack.ViewModel

      class IndexViewModel : ViewModel

    """.trimIndent())
  }

  @Test fun `project with new view builds correctly`() {
    val appName = "com.example.testapp2"
    val root = File(tempDir.root, appName)
    NewApplicationTask(root, appName, kalesFatJarPath).run()
    val appDir = File(root, appName)
    GenerateViewTask(appDir, "bar", "IndexView").run()
    val result = GradleRunner.create()
      .withProjectDir(appDir)
      .withArguments("jar")
      .build()
    assertThat(result.task(":jar")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
  }
}

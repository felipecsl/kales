package kales.cli

import com.github.ajalt.clikt.core.UsageError
import com.google.common.truth.Truth.assertThat
import org.junit.Test

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class GenerateControllerCommandRunnerTest {
  @get:Rule val tempDir = TemporaryFolder()

  @Test fun `test correctly creates controller class`() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    NewCommandRunner(root.absolutePath, appName).run()
    GenerateControllerCommandRunner(root, "bar").run()
    assertThat(File(root,
        "src/main/kotlin/com/example/testapp/app/controllers/BarController.kt").exists()).isTrue()
  }

  @Test(expected = UsageError::class)
  fun `invalid or inexistent app directory`() {
    GenerateControllerCommandRunner(File("."), "bar").run()
  }
}
package kales.cli

import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class NewCommandRunnerTest {
  @get:Rule val tempDir = TemporaryFolder()

  @Test fun `creates project files`() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    NewCommandRunner(root.absolutePath, appName).run()
    assertThat(File(root, "build.gradle").exists()).isTrue()
    assertThat(File(root, "gradlew").exists()).isTrue()
    assertThat(File(root, "gradle/wrapper/gradle-wrapper.properties").exists()).isTrue()
    assertThat(File(root, "gradle/wrapper/gradle-wrapper.jar").exists()).isTrue()
    assertThat(File(root, "src/main/kotlin/com/example/testapp").exists()).isTrue()
  }
}
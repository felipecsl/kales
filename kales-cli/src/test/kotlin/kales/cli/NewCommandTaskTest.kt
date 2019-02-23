package kales.cli

import com.google.common.truth.Truth.assertThat
import kales.cli.task.NewCommandTask
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class NewCommandTaskTest {
  @get:Rule val tempDir = TemporaryFolder()

  @Test fun `creates project files`() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    NewCommandTask(root, appName).run()
    assertThat(File(root, "build.gradle").exists()).isTrue()
    assertThat(File(root, "gradlew").exists()).isTrue()
    assertThat(File(root, "gradle/wrapper/gradle-wrapper.properties").exists()).isTrue()
    assertThat(File(root, "gradle/wrapper/gradle-wrapper.jar").exists()).isTrue()
    assertThat(File(root, "src/main/kotlin/com/example/testapp/app/controllers").exists()).isTrue()
    assertThat(File(root, "src/main/kotlin/com/example/testapp/db/migrate").exists()).isTrue()
    assertThat(File(root, "src/main/kotlin/com/example/testapp/app/views").exists()).isTrue()
    assertThat(File(root, "src/main/kotlin/com/example/testapp/app/models").exists()).isTrue()
  }
}
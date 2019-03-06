package kales.cli

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

class CliTest {
  @get:Rule val tempDir = TemporaryFolder()
  private val originalOut = System.out
  private val outContent = ByteArrayOutputStream()

  @Before fun setUp() {
    System.setOut(PrintStream(outContent))
    File("com.example").deleteRecursively()
  }

  @After fun tearDown() {
    System.setOut(originalOut)
  }

  @Test fun `test kales new`() {
    try {
      val workingDir = File(System.getProperty("user.dir"))
      New().parse(arrayOf("com.example"))
      val stdOut = outContent.toString()
      assertThat(stdOut).isEqualTo("""
           create com.example/build.gradle
           create com.example/src/main/kotlin/com/example/Main.kt
           create com.example/src/main/kotlin/com/example/routes.kt
           create com.example/src/main/kotlin/com/example/app/views/layouts/AppLayout.kt
           create com.example/src/main/resources/database.yml
           create com.example/gradle/wrapper/gradle-wrapper.properties
           create com.example/gradle/wrapper/gradle-wrapper.jar
           create com.example/gradlew

        New Kales project successfully initialized at '${workingDir.absolutePath}/com.example'.
        Happy coding!

      """.trimIndent())
    } finally {
      File("com.example").deleteRecursively()
    }
  }

  @Test fun `test identical files`() {
    try {
      val workingDir = File(System.getProperty("user.dir"))
      New().parse(arrayOf("com.example"))
      New().parse(arrayOf("com.example"))
      val stdOut = outContent.toString()
      assertThat(stdOut).isEqualTo("""
           create com.example/build.gradle
           create com.example/src/main/kotlin/com/example/Main.kt
           create com.example/src/main/kotlin/com/example/routes.kt
           create com.example/src/main/kotlin/com/example/app/views/layouts/AppLayout.kt
           create com.example/src/main/resources/database.yml
           create com.example/gradle/wrapper/gradle-wrapper.properties
           create com.example/gradle/wrapper/gradle-wrapper.jar
           create com.example/gradlew

        New Kales project successfully initialized at '${workingDir.absolutePath}/com.example'.
        Happy coding!
           identical com.example/build.gradle
           identical com.example/src/main/kotlin/com/example/Main.kt
           identical com.example/src/main/kotlin/com/example/routes.kt
           identical com.example/src/main/kotlin/com/example/app/views/layouts/AppLayout.kt
           identical com.example/src/main/resources/database.yml
           identical com.example/gradle/wrapper/gradle-wrapper.properties
           skip com.example/gradle/wrapper/gradle-wrapper.jar
           skip com.example/gradlew

        New Kales project successfully initialized at '${workingDir.absolutePath}/com.example'.
        Happy coding!

      """.trimIndent())
    } finally {
      File("com.example").deleteRecursively()
    }
  }
}
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
  }

  @After fun tearDown() {
    System.setOut(originalOut)
  }

  @Test fun `test kales new with absolute path`() {
    New().parse(arrayOf(tempDir.root.absolutePath, "com.example"))
    val stdOut = outContent.toString()
    val buildGradleRelativePath = File(tempDir.root, "build.gradle").relativePathToWorkingDir()
    val gradleWrapperPropsRelativePath = File(tempDir.root,
        "gradle/wrapper/gradle-wrapper.properties").relativePathToWorkingDir()
    val gradleWrapperJarRelativePath = File(tempDir.root,
        "gradle/wrapper/gradle-wrapper.jar").relativePathToWorkingDir()
    val gradlewRelativePath = File(tempDir.root, "gradlew").relativePathToWorkingDir()
    assertThat(stdOut).isEqualTo("""
             create $buildGradleRelativePath
             create $gradleWrapperPropsRelativePath
             create $gradleWrapperJarRelativePath
             create $gradlewRelativePath

          New Kales project successfully initialized at '${tempDir.root.absolutePath}'.
          Happy coding!

        """.trimIndent()
    )
  }

  @Test fun `test kales new with relative path`() {
    try {
      val workingDir = File(System.getProperty("user.dir"))
      New().parse(arrayOf("blah", "com.example"))
      val stdOut = outContent.toString()
      assertThat(stdOut).isEqualTo("""
           create blah/build.gradle
           create blah/gradle/wrapper/gradle-wrapper.properties
           create blah/gradle/wrapper/gradle-wrapper.jar
           create blah/gradlew

        New Kales project successfully initialized at '${workingDir.absolutePath}/blah'.
        Happy coding!

      """.trimIndent())
    } finally {
      File("blah").deleteRecursively()
    }
  }

  @Test fun `test identical files`() {
    try {
      val workingDir = File(System.getProperty("user.dir"))
      New().parse(arrayOf("blah", "com.example"))
      New().parse(arrayOf("blah", "com.example"))
      val stdOut = outContent.toString()
      assertThat(stdOut).isEqualTo("""
           create blah/build.gradle
           create blah/gradle/wrapper/gradle-wrapper.properties
           create blah/gradle/wrapper/gradle-wrapper.jar
           create blah/gradlew

        New Kales project successfully initialized at '${workingDir.absolutePath}/blah'.
        Happy coding!
           identical blah/build.gradle
           identical blah/gradle/wrapper/gradle-wrapper.properties
           skip blah/gradle/wrapper/gradle-wrapper.jar
           skip blah/gradlew

        New Kales project successfully initialized at '${workingDir.absolutePath}/blah'.
        Happy coding!

      """.trimIndent())
    } finally {
      File("blah").deleteRecursively()
    }
  }
}
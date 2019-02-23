package kales.cli.task

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DbMigrateTaskTest {
  @get:Rule val tempDir = TemporaryFolder()

  @Test fun run() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    NewCommandTask(root, appName).run()
  }
}
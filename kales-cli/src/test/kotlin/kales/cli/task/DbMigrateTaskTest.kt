package kales.cli.task

import com.google.common.truth.Truth.assertThat
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.h2.H2DatabasePlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.core.kotlin.mapTo
import org.junit.Rule
import kotlin.test.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DbMigrateTaskTest {
  @get:Rule val tempDir = TemporaryFolder()

  @Test fun `test migrate database by creating a table`() {
    val root = tempDir.root
    val appName = "com.example.testapp"
    NewApplicationTask(root, appName).run()
    val timestamp = SimpleDateFormat("yyyyMMddhhmmss").format(Date())
    val migrationsDir = "$root/$appName/src/main/kotlin/com/example/testapp/db/migrate"
    File(migrationsDir, "M${timestamp}_CreatePostsMigration.kts").writeText("""
      package $appName.db.migrate

      import kales.migrations.Migration

      class CreatePostsMigration : Migration() {
        override fun up() {
          createTable("posts") {
            varchar(columnName = "title", nullable = false)
            text("content", nullable = true)
          }
        }

        override fun down() {
          dropTable("posts")
        }
      }
    """.trimIndent())
    File("$root/$appName/src/main/resources/database.yml").writeText("""
      development:
        adapter: h2
        host: mem
        database: test
    """.trimIndent())
    val jdbi = Jdbi.create("jdbc:h2:mem:test")
        .installPlugin(H2DatabasePlugin())
        .installPlugin(KotlinPlugin())
    jdbi.withHandle<Any, RuntimeException> {
      DbMigrateTask(File(root, appName)).run()
      assertThat(it.createQuery("show tables").mapTo<String>().list())
          .containsExactly("SCHEMA_MIGRATIONS", "POSTS")
    }
  }
}

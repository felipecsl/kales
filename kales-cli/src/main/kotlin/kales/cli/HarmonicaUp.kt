package kales.cli

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.task.JarmonicaTaskMain
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.io.File

class HarmonicaUp(
    private val migrationsDirectory: File,
    private val connection: Connection
) : JarmonicaTaskMain() {
  fun run() {
    connection.use { connection ->
      connection.transaction {
        versionService.setupHarmonicaMigrationTable(connection)
      }
      for (file in migrationsDirectory.listFiles().sortedBy { it.name }) {
        val migrationVersion = file.name.split('_')[0]
        if (versionService.isVersionMigrated(connection, migrationVersion)) {
          continue
        }
        connection.transaction {
          val migration = readMigration(file.readText())
          migration.connection = connection
          migration.up()
          versionService.saveVersion(connection, migrationVersion)
        }
      }
    }
  }

  private fun readMigration(script: String): AbstractMigration {
    return engine.eval(removePackageStatement(script)) as AbstractMigration
  }

  private companion object {
    val engine by lazy {
      KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine
    }

    private fun removePackageStatement(script: String) =
        script.replace(Regex("^\\s*package\\s+.+"), "")
  }
}
package kales.cli

import com.improve_future.harmonica.core.AbstractMigration
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.service.VersionService
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.io.File

internal class HarmonicaUp(
    private val migrationsDirectory: File,
    private val connection: Connection
) {
  private val versionService = VersionService("schema_migrations")

  fun run() {
    connection.use { connection ->
      connection.transaction {
        versionService.setupHarmonicaMigrationTable(connection)
      }
      for (file in migrationsDirectory.listFiles().sortedBy { it.name }) {
        val migrationVersion = file.name.split('_').first()
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

  private fun readMigration(script: String) =
      engine.eval(removePackageStatement(script)) as AbstractMigration

  private companion object {
    val engine by lazy {
      KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine
    }

    private fun removePackageStatement(script: String) =
        script.replace(Regex("^\\s*package\\s+.+"), "")
  }
}
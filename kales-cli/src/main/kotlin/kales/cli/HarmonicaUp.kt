package kales.cli

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.service.VersionService
import kales.migrations.Migration
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import java.io.File
import javax.script.ScriptEngineManager

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

  private fun readMigration(script: String): Migration {
    val finalScript = addMigrationInstantiation(removePackageStatement(script))
    return engine.eval(finalScript) as Migration
  }

  private companion object {
    val engine by lazy {
      val factory = ScriptEngineManager().getEngineByExtension("kts").factory
      factory.scriptEngine as KotlinJsr223JvmLocalScriptEngine
    }

    /**
     * Apparently the Kotlin ScriptEngine needs an expression at the end of the file in order to
     * be able to return the class object after evaluating the migration file. So, we'll manually
     * scan for the migration class name and inject an instantiation at the end of the file.
     */
    private fun addMigrationInstantiation(script: String): String {
      val regex = "class ([a-zA-z0-9]+)".toRegex()
      for (line in script.lines()) {
        regex.find(line)?.let { matchResult ->
          val className = matchResult.groups[1]!!.value
          return "$script\n$className()"
        }
      }
      throw IllegalArgumentException("No class name found on migration file")
    }

    private fun removePackageStatement(script: String) =
      script.replace("^\\s*package\\s+.+".toRegex(), "")
  }
}

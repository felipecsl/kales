package kales.cli.task

import com.github.ajalt.clikt.core.UsageError
import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.core.Dbms
import kales.cli.HarmonicaUp
import kales.cli.relativePathFor
import kales.migrations.KalesDatabaseConfig
import java.io.File

class DbMigrateTask(workingDirectory: File) : KalesContextualTask(workingDirectory) {
  override fun run() {
    val databaseYml = File(resourcesDir, "database.yml")
    if (!databaseYml.exists()) {
      throw UsageError("database.yml file not found.\n" +
          "Plase make sure it exists under src/main/resources and try again")
    }
    val kalesDbConfig = KalesDatabaseConfig.fromDatabaseYml(databaseYml.inputStream())
    val harmonicaDbConfig = DbConfig {
      dbName = kalesDbConfig.database
      user = kalesDbConfig.username
      password = kalesDbConfig.password
      host = kalesDbConfig.password
      dbms = when (kalesDbConfig.adapter) {
        "postgresql" -> Dbms.PostgreSQL
        "mysql" -> Dbms.MySQL
        "sqlite" -> Dbms.SQLite
        "oracle" -> Dbms.Oracle
        "sqlserver" -> Dbms.SQLServer
        "h2" -> Dbms.H2
        else -> throw IllegalArgumentException("Unknown database adapter ${kalesDbConfig.adapter}")
      }
    }
    val dbMigrateDir = File(appDirectory.parentFile, relativePathFor("db", "migrate"))
    val connection = Connection(harmonicaDbConfig)
    val harmonicaUp = HarmonicaUp(dbMigrateDir, connection)
    harmonicaUp.run()
  }
}
package kales.cli.task

import com.improve_future.harmonica.core.Connection
import com.improve_future.harmonica.core.DbConfig
import com.improve_future.harmonica.core.Dbms
import kales.cli.HarmonicaUp
import kales.cli.relativePathFor
import java.io.File

class DbMigrateTask(workingDirectory: File) : KalesContextualTask(workingDirectory) {
  override fun run() {
    val kalesDbConfig = readDatabaseConfig()
    val harmonicaDbConfig = DbConfig {
      dbName = kalesDbConfig.database
      user = kalesDbConfig.username
      password = kalesDbConfig.password
      host = kalesDbConfig.host
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

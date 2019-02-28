package kales.cli.task

import kales.JdbiFactory
import kales.activemodel.use
import java.io.File

// Creates the database
class DbCreateTask(workingDir: File) : KalesContextualTask(workingDir) {
  override fun run() {
    val kalesDbConfig = readDatabaseConfig()
    val connString = kalesDbConfig.copy(database = "").toConnectionString()
    val jdbi = JdbiFactory.fromConnectionString(connString)
    jdbi.use {
      it.execute("CREATE DATABASE ${kalesDbConfig.database}")
    }
    println("Database ${kalesDbConfig.database} created.")
  }
}
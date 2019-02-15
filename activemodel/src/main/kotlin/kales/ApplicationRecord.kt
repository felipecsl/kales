package kales

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.yaml.snakeyaml.Yaml

abstract class ApplicationRecord {
  companion object {
    val JDBI: Jdbi = Jdbi.create(dbConnectionString()).installPlugins()

    @Suppress("UNCHECKED_CAST")
    private fun dbConnectionString(): String {
      val yaml = Yaml()
      val stream = ApplicationRecord::class.java.classLoader.getResourceAsStream("database.yml")
      val data = yaml.load<Map<String, Any>>(stream)
      val devData = data["development"] as Map<String, String>
      val host = devData["host"]
      val database = devData["database"]
      val username = devData["username"]
      val password = devData["password"]
      return "jdbc:postgresql://$host/$database?user=$username&password=$password"
    }

    inline fun <reified T : ApplicationRecord> allRecords(): List<T> {
      JDBI.open().use {
        val tableName = T::class.simpleName!!.toLowerCase()
        return it.createQuery("select * from ${tableName}s").mapTo<T>().list()
      }
    }
  }
}
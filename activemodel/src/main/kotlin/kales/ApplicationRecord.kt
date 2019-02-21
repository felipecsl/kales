package kales

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.h2.H2DatabasePlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.postgres.PostgresPlugin
import org.yaml.snakeyaml.Yaml
import kotlin.reflect.KClass

abstract class ApplicationRecord {
  companion object {
    val JDBI: Jdbi = Jdbi.create(dbConnectionString())
        .installPlugin(PostgresPlugin())
        .installPlugin(H2DatabasePlugin())
        .installPlugin(KotlinPlugin())

    @Suppress("UNCHECKED_CAST")
    private fun dbConnectionString(): String {
      val yaml = Yaml()
      val stream = ApplicationRecord::class.java.classLoader.getResourceAsStream("database.yml")
      val data = yaml.load<Map<String, Any>>(stream)
      val devData = data["development"] as Map<String, String>
      val adapter = devData["adapter"]
      val host = devData["host"]
      val database = devData["database"]
      return if (adapter == "h2") {
        "jdbc:$adapter:$host:$database"
      } else {
        val username = devData["username"] ?: ""
        val password = devData["password"] ?: ""
        "jdbc:$adapter://$host/$database?user=$username&password=$password"
      }
    }

    inline fun <reified T : ApplicationRecord> allRecords(): List<T> {
      JDBI.open().use {
        val tableName = T::class.toTableName()
        return it.createQuery("select * from $tableName").mapTo<T>().list()
      }
    }

    inline fun <reified T : ApplicationRecord> whereRecords(clause: Map<String, Any>): List<T> {
      JDBI.open().use {
        val tableName = T::class.toTableName()
        val whereClause = clause.keys.joinToString(" and ") { k -> "$k = :$k" }
        val query = it.createQuery("select * from $tableName where $whereClause")
        clause.forEach { k, v -> query.bind(k, v) }
        return query.mapTo<T>().list()
      }
    }

    inline fun <reified T : ApplicationRecord> createRecord(values: Map<String, Any>): Int {
      JDBI.open().use {
        val tableName = T::class.toTableName()
        val cols = values.keys.joinToString(prefix = "(", postfix = ")")
        val refs = values.keys.joinToString(prefix = "(", postfix = ")") { k -> ":$k" }
        val update = it.createUpdate("insert into $tableName $cols values $refs")
        values.forEach { k, v -> update.bind(k, v) }
        return update.execute()
      }
    }

    inline fun <reified T : ApplicationRecord> findRecord(id: Int): T {
      JDBI.open().use {
        val tableName = T::class.toTableName()
        return it.createQuery("select * from $tableName where id = :id")
            .bind("id", id)
            .mapTo<T>()
            .findOnly()
      }
    }

    fun KClass<*>.toTableName() = "${simpleName!!.toLowerCase()}s"
  }
}
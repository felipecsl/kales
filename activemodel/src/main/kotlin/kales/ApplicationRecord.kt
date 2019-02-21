package kales

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.h2.H2DatabasePlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.postgres.PostgresPlugin
import org.yaml.snakeyaml.Yaml

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
      // TODO handle muliple environments
      val devData = data["development"] as? Map<String, String> ?: throwMissingField("development")
      val adapter = devData["adapter"] ?: throwMissingField("adapter")
      val host = devData["host"] ?: throwMissingField("host")
      val database = devData["database"] ?: throwMissingField("database")
      return if (adapter == "h2") {
        "jdbc:$adapter:$host:$database"
      } else {
        val username = devData["username"] ?: ""
        val password = devData["password"] ?: ""
        "jdbc:$adapter://$host/$database?user=$username&password=$password"
      }
    }

    private fun throwMissingField(name: String): Nothing =
        throw IllegalArgumentException(
            "Please set a value for the field '$name' in the file database.yml")

    inline fun <reified T : ApplicationRecord> allRecords(): List<T> {
      useJdbi {
        val tableName = toTableName<T>()
        return it.createQuery("select * from $tableName").mapTo<T>().list()
      }
    }

    inline fun <reified T : ApplicationRecord> whereRecords(clause: Map<String, Any>): List<T> {
      useJdbi {
        val tableName = toTableName<T>()
        val whereClause = clause.keys.joinToString(" and ") { k -> "$k = :$k" }
        val query = it.createQuery("select * from $tableName where $whereClause")
        clause.forEach { k, v -> query.bind(k, v) }
        return query.mapTo<T>().list()
      }
    }

    inline fun <reified T : ApplicationRecord> createRecord(values: Map<String, Any>): Int {
      useJdbi {
        val tableName = toTableName<T>()
        val cols = values.keys.joinToString(prefix = "(", postfix = ")")
        val refs = values.keys.joinToString(prefix = "(", postfix = ")") { k -> ":$k" }
        val update = it.createUpdate("insert into $tableName $cols values $refs")
        values.forEach { k, v -> update.bind(k, v) }
        return update.execute()
      }
    }

    /** TODO I think Rails raises RecordNotFound in this case instead of returning null. Should we do the same? */
    inline fun <reified T : ApplicationRecord> findRecord(id: Int): T? {
      useJdbi {
        val tableName = toTableName<T>()
        return it.createQuery("select * from $tableName where id = :id")
            .bind("id", id)
            .mapTo<T>()
            .findFirst()
            .orElse(null)
      }
    }

    inline fun <T> useJdbi(block: (Handle) -> T) = JDBI.open().use { block(it) }

    inline fun <reified T> toTableName() = "${T::class.simpleName!!.toLowerCase()}s"
  }
}
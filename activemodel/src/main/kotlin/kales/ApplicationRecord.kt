package kales

import kales.activemodel.use
import kales.migrations.KalesDatabaseConfig
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo

abstract class ApplicationRecord {
  companion object {
    val JDBI: Jdbi = JdbiFactory.fromConnectionString(dbConnectionString())

    private fun dbConnectionString(): String {
      val stream = ApplicationRecord::class.java.classLoader.getResourceAsStream("database.yml")
      return KalesDatabaseConfig.fromDatabaseYml(stream).toConnectionString()
    }

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

    inline fun <T> useJdbi(block: (Handle) -> T) = JDBI.use(block)

    inline fun <reified T> toTableName() = "${T::class.simpleName!!.toLowerCase()}s"
  }
}
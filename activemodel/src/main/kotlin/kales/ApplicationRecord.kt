package kales

import kales.activemodel.CollectionModelAssociation
import kales.activemodel.SingleModelAssociation
import kales.activemodel.use
import kales.migrations.KalesDatabaseConfig
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.reflect.ColumnName
import org.jdbi.v3.core.result.ResultProducers.returningGeneratedKeys
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * Maps model classes to database records. Kales follows some conventions when dealing with models:
 * - All models are expected to have an `id` autoincrement primary key column
 * - Foreign key columns are mapped using `<model_name>_id` column naming format
 * - The table name is a plural of the model name, eg: `User` -> table is `users`. We don't do any
 * fancy pluralization for now, just naively append `s` at the end, which means the table for `Hero`
 * would be `heros`, awkwardly.
 */
abstract class ApplicationRecord {
  companion object {
    val JDBI: Jdbi = JdbiFactory.fromConnectionString(dbConnectionString())

    private fun dbConnectionString(): String {
      val stream = ApplicationRecord::class.java.classLoader.getResourceAsStream("database.yml")
      return KalesDatabaseConfig.fromDatabaseYml(stream).toConnectionString()
    }

    /** Returns a list with all records in the table (potentially dangerous for big tables!) */
    inline fun <reified T : ApplicationRecord> allRecords(): List<T> {
      useJdbi {
        val tableName = toTableName<T>()
        val columnNames = columnNames<T>()
        return it.createQuery("select $columnNames from $tableName")
            .mapTo<T>()
            .list()
      }
    }

    /** Returns only the records matching the provided selection criteria */
    inline fun <reified T : ApplicationRecord> whereRecords(clause: Map<String, Any?>): List<T> {
      useJdbi {
        val tableName = toTableName<T>()
        // TODO There is ambiguity with null values in the where clause here.
        // Since currently all null values are filtered out, it's impossible to query for rows with
        // a given column being equals to NULL. We need to add an abstraction to map that case.
        val whereClause = clause.filterValues { v -> v != null }
            .keys
            .joinToString(" and ") { k -> "$k = :$k" }
        val columnNames = columnNames<T>()
        val query = it.createQuery(
            "select $columnNames from $tableName where $whereClause")
        clause.forEach { k, v -> query.bind(k, v) }
        return query.mapTo<T>().list()
      }
    }

    inline fun <reified T : ApplicationRecord> createRecord(values: Map<String, Any>): T {
      useJdbi {
        val tableName = toTableName<T>()
        val cols = values.keys.joinToString(prefix = "(", postfix = ")")
        val refs = values.keys.joinToString(prefix = "(", postfix = ")") { k -> ":$k" }
        val update = it.createUpdate("insert into $tableName $cols values $refs")
        values.forEach { k, v -> update.bind(k, v) }
        return update.execute(returningGeneratedKeys())
            .mapTo<Int>()
            .findFirst()
            .map { id -> findRecord<T>(id) }
            .orElseThrow { RuntimeException("Failed to create record.") }!!
      }
    }

    /**
     * TODO I think Rails raises RecordNotFound in this case instead of returning null.
     * Should we do the same?
     */
    inline fun <reified T : ApplicationRecord> findRecord(id: Int): T? {
      useJdbi {
        val tableName = toTableName<T>()
        val columnNames = columnNames<T>()
        return it.createQuery("select $columnNames from $tableName where id = :id")
            .bind("id", id)
            .mapTo<T>()
            .findFirst()
            .orElse(null)
      }
    }

    /**
     * Returns all the param names for the provided [ApplicationRecord] [KClass] for use with
     * `select` statements. We can't just `select *` because relationships are a special case.
     * [CollectionModelAssociation] properties are manually injected into the query by selecting the `id`
     * column `as <propertyName>`. This allows us to "fool" JDBI into thinking there's an extra
     * column for that property, so we can hook into that from [CollectionModelAssociationColumnMapper] and
     * properly hook the relationship to the other model.
     */
    inline fun <reified T : ApplicationRecord> columnNames(): String {
      val klass = T::class
      val constructor = klass.primaryConstructor
          ?: throw IllegalArgumentException("Please define a primary constructor for $this")
      val directProps = constructor.parameters.mapNotNull { it.paramName() }
      return directProps.joinToString(",")
    }

    fun KParameter.paramName(): String? {
      val propName = findAnnotation<ColumnName>()?.value ?: name
      val classifier = type.classifier
      return when (classifier) {
        CollectionModelAssociation::class -> "id as $propName"
        SingleModelAssociation::class -> "${name}_id as $propName"
        else -> propName
      }
    }

    inline fun <T> useJdbi(block: (Handle) -> T) = JDBI.use(block)

    inline fun <reified T : ApplicationRecord> toTableName() =
        "${T::class.simpleName!!.toLowerCase()}s"
  }
}
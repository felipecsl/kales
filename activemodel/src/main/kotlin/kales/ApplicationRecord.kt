package kales

import kales.activemodel.ModelCollectionAssociation
import kales.activemodel.use
import kales.migrations.KalesDatabaseConfig
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.reflect.ColumnName
import org.jdbi.v3.core.result.ResultProducers.returningGeneratedKeys
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField

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
        val columnNames = columnNames<T>()
        return it.createQuery("select $columnNames from $tableName")
            .mapTo<T>()
            .list()
      }
    }

    inline fun <reified T : ApplicationRecord> whereRecords(clause: Map<String, Any>): List<T> {
      useJdbi {
        val tableName = toTableName<T>()
        val whereClause = clause.keys.joinToString(" and ") { k -> "$k = :$k" }
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

    /** TODO I think Rails raises RecordNotFound in this case instead of returning null. Should we do the same? */
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
     * [ModelCollectionAssociation] properties are manually injected into the query by selecting the `id`
     * column `as <propertyName>`. This allows us to "fool" JDBI into thinking there's an extra
     * column for that property, so we can hook into that from [ModelCollectionColumnMapper] and
     * properly hook the relationship to the other model.
     */
    inline fun <reified T : ApplicationRecord> columnNames(): String {
      val klass = T::class
      val constructor = klass.primaryConstructor
          ?: throw IllegalArgumentException("Please define a primary constructor for $this")
      val directProps = constructor
          .parameters
          .mapNotNull { it.paramName() }
      val relationProps = klass.memberProperties
          .mapNotNull { it as? KMutableProperty1<*, *> }
          .filter { !constructor.parameters.any { param -> param.paramName() == it.propName() } }
          .map { "id as ${it.propName()}" } // map relation props to the ID column for JDBI
      return (directProps + relationProps).joinToString(",")
    }

    fun KParameter.paramName() =
        findAnnotation<ColumnName>()?.value ?: name

    fun KMutableProperty1<*, *>.propName() =
        javaField?.getAnnotation(ColumnName::class.java)?.value ?: name

    inline fun <T> useJdbi(block: (Handle) -> T) = JDBI.use(block)

    inline fun <reified T : ApplicationRecord> toTableName() =
        "${T::class.simpleName!!.toLowerCase()}s"
  }
}
package kales.internal

import kales.ApplicationRecord
import kales.HasManyAssociationColumnMapper
import kales.activemodel.BelongsToAssociation
import kales.activemodel.HasManyAssociation
import kales.activemodel.NoneId
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.mapper.reflect.ColumnName
import org.jdbi.v3.core.statement.Query
import org.jdbi.v3.core.statement.Update
import java.lang.IllegalStateException
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

class RecordQueryBuilder(
    private val handle: Handle,
    private val applicationRecordClass: KApplicationRecordClass,
    private val recordUpdater: RecordUpdater = RecordUpdater(handle, applicationRecordClass)
) {
  private val tableName = applicationRecordClass.tableName

  private val constructor = applicationRecordClass.constructor

  fun where(clause: Map<String, Any?>): Query {
    // TODO There is ambiguity with null values in the where clause here.
    // Since currently all null values are filtered out, it's impossible to query for rows with
    // a given column being equals to NULL. We need to add an abstraction to map that case.
    val whereClause = clause
        .filterValues { v -> v != null }
        .keys
        .joinToString(" and ") { k -> "$k = :$k" }
    val columnNames = columnNames()
    val queryString = "select $columnNames from $tableName where $whereClause"
    return handle.createQuery(queryString).also { query ->
      clause.forEach { (k, v) -> query.bind(k, v) }
    }
  }

  fun allRecords(): Query {
    val columnNames = columnNames()
    val queryString = "select $columnNames from $tableName"
    return handle.createQuery(queryString)
  }

  fun findRecord(id: Int): Query {
    val columnNames = columnNames()
    val queryString = "select $columnNames from $tableName where id = :id"
    return handle.createQuery(queryString).bind("id", id)
  }

  fun create(values: Map<String, Any?>): Update {
    val nonNullPairs = values.filterValues { it != null }
    val nonNullKeys = nonNullPairs.keys
    val cols = nonNullKeys.joinToString(prefix = "(", postfix = ")")
    val refs = nonNullKeys.joinToString(prefix = "(", postfix = ")") { k -> ":$k" }
    val queryString = "insert into $tableName $cols values $refs"
    return handle.createUpdate(queryString).also { insert ->
      nonNullPairs.forEach { (k, v) -> insert.bind(k, v) }
    }
  }

  fun update(record: ApplicationRecord) {
    recordUpdater.update(record)
  }

  /**
   * Destroys (deletes) a record from the database. If the record's ID is missing (none/unsaved),
   * this will throw `IllegalStateException` instead.
   */
  fun destroy(record: ApplicationRecord) {
    if (record.id is NoneId) {
      throw IllegalStateException(
          "Record does not have an ID, has it been previously saved in the DB?")
    }
    recordUpdater.destroy(record)
  }

  /**
   * Returns all the param names for the provided [ApplicationRecord] [KClass] for use with
   * `select` statements. We can't just `select *` because relationships are a special case.
   * [HasManyAssociation] properties are manually injected into the query by selecting the `id`
   * column `as <propertyName>`. This allows us to "fool" JDBI into thinking there's an extra
   * column for that property, so we can hook into that from [HasManyAssociationColumnMapper] and
   * properly hook the relationship to the other model.
   */
  private fun columnNames(): String {
    return constructor.parameters.mapNotNull { it.paramName() }.joinToString(",")
  }

  private fun KParameter.paramName(): String? {
    val propName = findAnnotation<ColumnName>()?.value ?: name
    return when (type.classifier) {
      HasManyAssociation::class -> "id as $propName"
      BelongsToAssociation::class -> "${name}_id as $propName"
      else -> propName
    }
  }
}
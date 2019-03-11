package kales.internal

import kales.ApplicationRecord
import kales.CollectionModelAssociationColumnMapper
import kales.activemodel.HasManyAssociation
import kales.activemodel.BelongsToAssociation
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.mapper.reflect.ColumnName
import org.jdbi.v3.core.statement.Query
import org.jdbi.v3.core.statement.Update
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

class RecordQueryBuilder(
    private val handle: Handle,
    private val klass: KClass<out ApplicationRecord>
) {
  private val tableName = "${klass.simpleName!!.toLowerCase()}s"

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
      clause.forEach { k, v -> query.bind(k, v) }
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

  fun update(values: Map<String, Any>): Update {
    val cols = values.keys.joinToString(prefix = "(", postfix = ")")
    val refs = values.keys.joinToString(prefix = "(", postfix = ")") { k -> ":$k" }
    val queryString = "insert into $tableName $cols values $refs"
    return handle.createUpdate(queryString).also { update ->
      values.forEach { k, v -> update.bind(k, v) }
    }
  }

  /**
   * Returns all the param names for the provided [ApplicationRecord] [KClass] for use with
   * `select` statements. We can't just `select *` because relationships are a special case.
   * [HasManyAssociation] properties are manually injected into the query by selecting the `id`
   * column `as <propertyName>`. This allows us to "fool" JDBI into thinking there's an extra
   * column for that property, so we can hook into that from [CollectionModelAssociationColumnMapper] and
   * properly hook the relationship to the other model.
   */
  private fun columnNames(): String {
    val constructor = klass.primaryConstructor
        ?: throw IllegalArgumentException("Please define a primary constructor for $this")
    val directProps = constructor.parameters.mapNotNull { it.paramName() }
    return directProps.joinToString(",")
  }

  private fun KParameter.paramName(): String? {
    val propName = findAnnotation<ColumnName>()?.value ?: name
    val classifier = type.classifier
    return when (classifier) {
      HasManyAssociation::class -> "id as $propName"
      BelongsToAssociation::class -> "${name}_id as $propName"
      else -> propName
    }
  }
}
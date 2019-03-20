package kales.internal

import kales.ApplicationRecord
import kales.activemodel.Association
import kales.activemodel.BelongsToAssociation
import kales.activemodel.HasManyAssociation
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.statement.Update
import java.lang.reflect.ParameterizedType
import java.sql.SQLException
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaType

class RecordUpdater(
    private val handle: Handle,
    private val recordClass: KApplicationRecordClass
) {
  private val tableName = recordClass.tableName

  private val constructor = recordClass.constructor

  fun update(record: ApplicationRecord): Update {
    if (record.javaClass.kotlin != recordClass.klass) {
      throw IllegalArgumentException("Record class ${record.javaClass.kotlin} does not match $recordClass")
    }
    return updateRecordColumns(record).also {
      updateRecordAssociations(record)
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun updateRecordAssociations(record: ApplicationRecord) {
    val properties = record.javaClass.kotlin.declaredMemberProperties
    recordClass.hasManyAssociations.forEach { assocParam ->
      val associationType = assocParam.type.javaType as ParameterizedType
      val fromType = associationType.actualTypeArguments[0] as Class<out ApplicationRecord>
      val toType = associationType.actualTypeArguments[1] as Class<out ApplicationRecord>
      val toRecordClass = KApplicationRecordClass(toType.kotlin)
      val fromRecordClass = KApplicationRecordClass(fromType.kotlin)
      val assocValue = properties.first { it.name == assocParam.name }.get(record) as Association
      when (assocValue) {
        is HasManyAssociation<*, *> ->
          updateHasManyAssociation(record, fromRecordClass, toRecordClass, assocValue)
        is BelongsToAssociation<*> ->
          updateBelongsToAssociation(record, fromRecordClass, toRecordClass, assocValue)
        else -> throw IllegalArgumentException("Invalid association type found $assocValue")
      }
    }
  }

  private fun updateBelongsToAssociation(
      record: ApplicationRecord,
      fromRecordClass: KApplicationRecordClass,
      toRecordClass: KApplicationRecordClass,
      assocValue: BelongsToAssociation<*>
  ) {
    TODO()
  }

  private fun updateHasManyAssociation(
      fromRecord: ApplicationRecord,
      fromRecordClass: KApplicationRecordClass,
      toRecordClass: KApplicationRecordClass,
      assocValue: HasManyAssociation<*, *>
  ) {
    assocValue.forEach { assoc ->
      // set the foreign key column of each element of the has many association to point to the
      // "fromRecord" ID. These updates are executed immediately
      updateRecordSingleColumn(toRecordClass.tableName, fromRecordClass.foreignKeyColumnName,
          fromRecord.id, assoc.id)
    }
  }

  private fun updateRecordColumns(record: ApplicationRecord): Update {
    val properties = record.javaClass.kotlin.declaredMemberProperties
    // When updating a record, we need to take some precautions around which columns we can update:
    // - We need to filter out association columns (eg HasManyAssociation) since they don't directly
    //   map to a record column and are sort of a "synthetic" property, so they needs to be handled
    //   separately during an update
    // - We need filter out the "id" column since it cannot be updated (it's set to autoincrement by
    //   default)
    val validParameterNames = recordClass.directParameters
        .mapNotNull { it.name }
    val colsToUpdate = validParameterNames.filter { it != "id" }
        .joinToString(", ") { k -> "$k = :$k" }
    val updateStatement = "update $tableName set $colsToUpdate where id = :id"
    return handle.createUpdate(updateStatement).also { update ->
      val colunmNamesAndValues = validParameterNames.associate { param ->
        param to properties.first { it.name == param }.get(record)
      }
      colunmNamesAndValues.forEach { k, v -> update.bind(k, v) }
    }
  }

  /**
   * Updates a single column [columnName] to [columnValue] from the record with ID [recordId] on
   * table [tableName]
   */
  private fun updateRecordSingleColumn(
      tableName: String,
      columnName: String,
      columnValue: Int,
      recordId: Int
  ) {
    val updateStatement = "update $tableName set $columnName = :$columnName where id = :id"
    handle.createUpdate(updateStatement)
        .bind(columnName, columnValue)
        .bind("id", recordId)
        .also { update ->
          if (update.execute() != 1) {
            throw SQLException("Failed to update record $this")
          }
        }
  }
}
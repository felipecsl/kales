package kales

import kales.activemodel.BelongsToAssociation
import kales.internal.LazyBelongsToAssociation
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.ResultSet
import java.util.logging.Logger

class BelongsToAssociationColumnMapper(
  private val type: Type
) : ColumnMapper<BelongsToAssociation<*>> {
  @Suppress("UNCHECKED_CAST")
  override fun map(
    resultSet: ResultSet,
    columnNumber: Int,
    context: StatementContext
  ): BelongsToAssociation<*> =
    if (type is ParameterizedType && type.rawType == BelongsToAssociation::class.java) {
      val toType = type.actualTypeArguments.first()
      val toRecordId = resultSet.getInt(columnNumber)
      logger.info("Resolving belongs to association with ID $toRecordId to $toType")
      val toKlass = (toType as Class<ApplicationRecord>).kotlin
      LazyBelongsToAssociation(toKlass, toRecordId)
    } else {
      throw IllegalArgumentException("Invalid type found $type")
    }

  companion object {
    private val logger = Logger.getLogger(BelongsToAssociationColumnMapper::class.simpleName)
  }
}
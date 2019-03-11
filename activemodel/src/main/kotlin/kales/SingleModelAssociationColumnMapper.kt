package kales

import kales.activemodel.BelongsToAssociation
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.ResultSet
import java.util.logging.Logger

class SingleModelAssociationColumnMapper(
    private val type: Type
) : ColumnMapper<BelongsToAssociation<*>> {
  override fun map(
      resultSet: ResultSet,
      columnNumber: Int,
      context: StatementContext
  ): BelongsToAssociation<*> =
      if (type is ParameterizedType && type.rawType == BelongsToAssociation::class.java) {
        val toType = type.actualTypeArguments.first()
        val fromModelId = resultSet.getInt(columnNumber)
        logger.info("Resolving model single association with ID $fromModelId to $toType")
        // TODO implement this
        BelongsToAssociation.empty<ApplicationRecord>()
      } else {
        throw IllegalArgumentException("Invalid type found $type")
      }

  companion object {
    private val logger = Logger.getLogger(SingleModelAssociationColumnMapper::class.simpleName)
  }
}
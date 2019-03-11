package kales

import kales.activemodel.CollectionModelAssociation
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.ResultSet
import java.util.logging.Logger

internal class CollectionModelAssociationColumnMapper(
    private val type: Type
) : ColumnMapper<CollectionModelAssociation<*, *>> {
  @Suppress("UNCHECKED_CAST")
  override fun map(
      resultSet: ResultSet,
      columnNumber: Int,
      context: StatementContext
  ) =
      if (type is ParameterizedType && type.rawType == CollectionModelAssociation::class.java) {
        val typeArguments = type.actualTypeArguments
        val fromType = typeArguments.first()
        val toType = typeArguments.last()
        val fromModelId = resultSet.getInt(columnNumber)
        logger.info("Resolving model collection association from " +
            "$fromType with ID $fromModelId to $toType")
        val fromKlass = (fromType as Class<ApplicationRecord>).kotlin
        val toKlass = (toType as Class<ApplicationRecord>).kotlin
        LazyCollectionModelAssociation(fromKlass, toKlass, fromModelId)
      } else {
        throw IllegalArgumentException("Invalid type found $type")
      }

  companion object {
    private val logger = Logger.getLogger(CollectionModelAssociationColumnMapper::class.simpleName)
  }
}

package kales

import kales.ApplicationRecord.Companion.JDBI
import kales.activemodel.CollectionModelAssociation
import kales.activemodel.use
import kales.internal.RecordQueryBuilder
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.ResultSet
import java.util.logging.Logger
import kotlin.reflect.KClass

internal class CollectionModelAssociationColumnMapper(
    private val type: Type
) : ColumnMapper<CollectionModelAssociation<*, *>> {
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
        logger.info(
            "Resolving model collection association from $fromType with ID $fromModelId to $toType")
        object : CollectionModelAssociation<ApplicationRecord, ApplicationRecord> {
          override val fromKlass: KClass<ApplicationRecord>
            get() = (fromType as Class<ApplicationRecord>).kotlin

          override val collection: List<ApplicationRecord>
            get() {
              JDBI.use {
                val toKlass = toType as Class<out ApplicationRecord>
                val queryBuilder = RecordQueryBuilder(it, toKlass.kotlin)
                // We're assuming the property name matches the class name - That should always be
                // the case, e.: For table `Posts`, foreign key is `post_id`
                val clause = mapOf("${fromKlass.simpleName!!.toLowerCase()}_id" to fromModelId)
                queryBuilder.where(clause).let { query ->
                  return query.mapTo(toType).list()
                }
              }
            }
        }
      } else {
        throw IllegalArgumentException("Invalid type found $type")
      }

  companion object {
    private val logger = Logger.getLogger(CollectionModelAssociationColumnMapper::class.simpleName)
  }
}

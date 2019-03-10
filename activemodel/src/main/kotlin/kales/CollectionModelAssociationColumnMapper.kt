package kales

import kales.ApplicationRecord.Companion.JDBI
import kales.activemodel.CollectionModelAssociation
import kales.activemodel.use
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberExtensionFunctions

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
        println("Model type is from $fromType with ID $fromModelId to $toType")
        // TODO implement this
        object : CollectionModelAssociation<ApplicationRecord, ApplicationRecord> {
          override val fromKlass: KClass<ApplicationRecord>
            get() = TODO("not implemented")
          override val collection: List<ApplicationRecord>
            get() {
              JDBI.use {
                val toKlass = toType as Class<*>
                toKlass.kotlin.declaredFunctions.filter { it.name == "where" }
              }
            }
        }
      } else {
        throw IllegalArgumentException("Invalid type found $type")
      }
}

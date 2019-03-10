package kales

import kales.activemodel.SingleModelAssociation
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.ResultSet

class SingleModelAssociationColumnMapper(
    private val type: Type
) : ColumnMapper<SingleModelAssociation<*>> {
  override fun map(
      resultSet: ResultSet,
      columnNumber: Int,
      context: StatementContext
  ): SingleModelAssociation<*> =
      if (type is ParameterizedType && type.rawType == SingleModelAssociation::class.java) {
        val toType = type.actualTypeArguments.first()
        val fromModelId = resultSet.getInt(columnNumber)
        println("SingleModelAssociationColumnMapper Model type is $toType with ID $fromModelId to $toType")
        // TODO implement this
        SingleModelAssociation.empty<ApplicationRecord>()
      } else {
        throw IllegalArgumentException("Invalid type found $type")
      }

}
package kales

import kales.activemodel.ModelCollectionAssociation
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.ResultSet

internal class ModelCollectionColumnMapper(private val type: Type) : ColumnMapper<ModelCollectionAssociation<*, *>> {
  override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext): ModelCollectionAssociation<*, *> {
    return if (type is ParameterizedType) {
      val typeArguments = type.actualTypeArguments
      val fromType = typeArguments.first()
      val toType = typeArguments.last()
      val fromModelId = r.getInt(columnNumber)
      println("Model type is from $fromType with ID $fromModelId to $toType")
      // TODO
      ModelCollectionAssociation.empty<ApplicationRecord, ApplicationRecord>()
    } else {
      throw IllegalArgumentException(type.toString())
    }
  }
}

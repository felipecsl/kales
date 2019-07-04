package kales.internal

import kales.ApplicationRecord
import kales.activemodel.BelongsToAssociation
import kales.activemodel.HasManyAssociation
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

class KApplicationRecordClass(val klass: KClass<out ApplicationRecord>) {
  val asSymbol = klass.simpleName!!.toLowerCase()

  /** Video class -> video
   * TODO Pluralize irregular words (#49)
   * TODO Camelize it (#50)
   */
  val tableName = asSymbol.pluralize()

  /** eg: Video class -> video_id, Post class -> post_id */
  val foreignKeyColumnName = "${asSymbol}_id"

  val constructor = klass.primaryConstructor
      ?: throw IllegalArgumentException("Please define a primary constructor for $this")

  /** Returns a [List] of all [KParameter] directly defined on this class (except associations) */
  val directParameters = constructor.parameters.filterNot { it.isAssociation() }

  /** Returns a [List] of all [KParameter] associations for this [ApplicationRecord] */
  val associations = constructor.parameters.filter { it.isAssociation() }

  val hasManyAssociations = associations.filter {
    (it.type.javaType as? ParameterizedType)?.rawType == HasManyAssociation::class.java
  }

  val belongsToAssociations = associations.filter {
    (it.type.javaType as? ParameterizedType)?.rawType == BelongsToAssociation::class.java
  }

  private fun KParameter.isAssociation(): Boolean {
    val javaType = type.javaType
    return javaType is ParameterizedType
        && (javaType.rawType == HasManyAssociation::class.java
        || javaType.rawType == BelongsToAssociation::class.java)
  }
}

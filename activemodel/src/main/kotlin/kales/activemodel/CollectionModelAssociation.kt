package kales.activemodel

import kales.ApplicationRecord
import kotlin.reflect.KClass

/** Represents a one-to-many association between models [F] and [T] respectively */
interface CollectionModelAssociation<F : ApplicationRecord, T : ApplicationRecord> {
  val fromKlass: KClass<F>

  val collection: List<T>

  companion object {
    inline fun <reified F : ApplicationRecord, T : ApplicationRecord> empty() =
        object : CollectionModelAssociationImpl<F, T>(F::class) {
          override val collection: List<T> = listOf()
        }
  }
}

abstract class CollectionModelAssociationImpl<F : ApplicationRecord, T : ApplicationRecord>(
    override val fromKlass: KClass<F>
) : CollectionModelAssociation<F, T> {
  override fun equals(other: Any?) =
      if (other is CollectionModelAssociation<*, *>) {
        collection == other.collection
      } else {
        false
      }

  override fun hashCode() = collection.hashCode()
}
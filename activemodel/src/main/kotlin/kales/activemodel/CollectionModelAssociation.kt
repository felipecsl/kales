package kales.activemodel

import kales.ApplicationRecord
import kotlin.reflect.KClass

/** Represents a one-to-many association between models [F] and [T] respectively */
interface CollectionModelAssociation<F : ApplicationRecord, T : ApplicationRecord> {
  val fromKlass: KClass<F>

  val collection: List<T>

  companion object {
    inline fun <reified F : ApplicationRecord, T : ApplicationRecord> empty() =
        object : CollectionModelAssociation<F, T> {
          override val collection: List<T> = listOf()

          override fun equals(other: Any?) =
              if (other is CollectionModelAssociation<*, *>) {
                collection == other.collection
              } else {
                false
              }

          override fun hashCode() = collection.hashCode()

          override val fromKlass = F::class
        }
  }
}
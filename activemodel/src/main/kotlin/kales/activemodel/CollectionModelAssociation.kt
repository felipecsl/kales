package kales.activemodel

import kales.ApplicationRecord
import kotlin.reflect.KClass

/** Represents a one-to-many association between models [F] and [T] respectively */
interface CollectionModelAssociation<F : ApplicationRecord, T : ApplicationRecord> : Collection<T> {
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
  override val size: Int
    get() = collection.size

  override fun contains(element: T) = collection.contains(element)

  override fun containsAll(elements: Collection<T>) = collection.containsAll(elements)

  override fun isEmpty() = collection.isEmpty()

  override fun iterator() = collection.iterator()

  override fun equals(other: Any?) =
      if (other is CollectionModelAssociation<*, *>) {
        collection == other.collection
      } else {
        false
      }

  override fun hashCode() = collection.hashCode()
}
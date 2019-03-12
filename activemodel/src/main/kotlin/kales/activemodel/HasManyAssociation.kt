package kales.activemodel

import kales.ApplicationRecord
import kotlin.reflect.KClass

/** Represents a one-to-many association between models [F] and [T] respectively */
interface HasManyAssociation<F : ApplicationRecord, T : ApplicationRecord> : Collection<T> {
  val fromKlass: KClass<F>

  val collection: List<T>

  companion object {
    inline fun <reified F : ApplicationRecord, T : ApplicationRecord> empty() =
        object : HasManyAssociationImpl<F, T>(F::class) {
          override val collection: List<T> = listOf()
        }
  }
}

abstract class HasManyAssociationImpl<F : ApplicationRecord, T : ApplicationRecord>(
    override val fromKlass: KClass<F>
) : HasManyAssociation<F, T> {
  override val size: Int
    get() = collection.size

  override fun contains(element: T) = collection.contains(element)

  override fun containsAll(elements: Collection<T>) = collection.containsAll(elements)

  override fun isEmpty() = collection.isEmpty()

  override fun iterator() = collection.iterator()

  override fun equals(other: Any?) =
      if (other is HasManyAssociation<*, *>) {
        // We need to perform a "shallow" equals here because otherwise we'd cause a stack overflow
        // due to the wya HasMany/BelongsTo relationships work between models, by nature they have
        // to reference each other, causing a circular dependency and, thus, stack overflow.
        collection.map { it.id } == other.collection.map { it.id }
      } else {
        false
      }

  override fun hashCode() = collection.hashCode()
}
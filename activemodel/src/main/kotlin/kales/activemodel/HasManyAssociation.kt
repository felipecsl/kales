package kales.activemodel

import kales.ApplicationRecord
import java.util.*
import kotlin.reflect.KClass

/** Represents a one-to-many association between models [F] and [T] respectively */
interface HasManyAssociation<F : ApplicationRecord, T : ApplicationRecord>
  : MutableCollection<T>, Association {
  val fromKlass: KClass<F>

  val collection: MutableList<T>

  companion object {
    inline fun <reified F : ApplicationRecord, T : ApplicationRecord> empty() =
        object : HasManyAssociationImpl<F, T>(F::class) {
          override val collection: MutableList<T> = mutableListOf()
        }
  }
}

abstract class HasManyAssociationImpl<F : ApplicationRecord, T : ApplicationRecord>(
    override val fromKlass: KClass<F>
) : HasManyAssociation<F, T> {
  override fun add(element: T) = collection.add(element)

  override fun addAll(elements: Collection<T>) = collection.addAll(elements)

  override fun clear() {
    collection.clear()
  }

  override fun remove(element: T) = collection.remove(element)

  override fun removeAll(elements: Collection<T>) = collection.removeAll(elements)

  override fun retainAll(elements: Collection<T>) = collection.retainAll(elements)

  override val size get() = collection.size

  override fun contains(element: T) = collection.contains(element)

  override fun containsAll(elements: Collection<T>) = collection.containsAll(elements)

  override fun isEmpty() = collection.isEmpty()

  override fun iterator() = collection.iterator()

  override fun equals(other: Any?) =
      if (other is HasManyAssociation<*, *>) {
        // We need to perform a "shallow" equals here because otherwise we'd cause a stack overflow
        // due to the way HasMany/BelongsTo relationships work between models, by nature they have
        // to reference each other, causing a circular dependency and, thus, stack overflow.
        collection.map { it.id } == other.collection.map { it.id }
      } else {
        false
      }

  override fun hashCode() = collection.map { Objects.hash(it.id) }.sum()
}
package kales.activemodel

import kales.ApplicationRecord
import kotlin.reflect.KClass

/** Represents a one-to-many association between models [F] and [T] respectively */
interface ModelCollectionAssociation<F : ApplicationRecord, T : ApplicationRecord> : Iterable<T> {
  val fromKlass: KClass<F>

  companion object {
    inline fun <reified F : ApplicationRecord, T : ApplicationRecord> empty() =
        object : ModelCollectionAssociation<F, T> {
          override val fromKlass = F::class

          override fun iterator(): Iterator<T> {
            TODO("not implemented")
          }
        }
  }
}
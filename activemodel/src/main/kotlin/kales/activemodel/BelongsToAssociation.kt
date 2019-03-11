package kales.activemodel

import kales.ApplicationRecord

interface BelongsToAssociation<T : ApplicationRecord> {
  val value: T?

  companion object {
    fun <T : ApplicationRecord> empty() = object : BelongsToAssociation<T> {
      override fun equals(other: Any?) =
          if (other is BelongsToAssociation<*>) {
            value == other.value
          } else {
            false
          }

      override fun hashCode() = value.hashCode()

      override val value: T? = null
    }
  }
}
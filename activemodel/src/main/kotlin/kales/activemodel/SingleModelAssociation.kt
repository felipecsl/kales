package kales.activemodel

import kales.ApplicationRecord

interface SingleModelAssociation<T : ApplicationRecord> {
  val value: T?

  companion object {
    fun <T : ApplicationRecord> empty() = object : SingleModelAssociation<T> {
      override fun equals(other: Any?) =
          if (other is SingleModelAssociation<*>) {
            value == other.value
          } else {
            false
          }

      override fun hashCode() = value.hashCode()

      override val value: T? = null
    }
  }
}
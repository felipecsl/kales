package kales.activemodel

import kales.ApplicationRecord

/**
 * Represents an association where a model A's table contains a foreign key to another model B's
 * [ApplicationRecord] object, in a "A belongs to B" fashion.
 */
interface BelongsToAssociation<T : ApplicationRecord> : Association {
  var value: T?

  companion object {
    inline fun <reified T : ApplicationRecord> empty() =
      object : BelongsToAssociationImpl<T>() {
        override var value: T? = null
      }
  }
}

abstract class BelongsToAssociationImpl<T : ApplicationRecord> : BelongsToAssociation<T> {
  override fun equals(other: Any?) =
    if (other is BelongsToAssociation<*>) {
      value?.id == other.value?.id
    } else {
      false
    }

  override fun hashCode() = value?.id.hashCode()

  override fun toString(): String {
    val elementClassName = value?.javaClass?.simpleName ?: "NullElement"
    val idString = if (value != null) "(id=${value?.id})" else ""
    return "BelongsToAssociationImpl($elementClassName$idString)"
  }
}
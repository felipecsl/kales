package kales.activemodel

import kales.ApplicationRecord

/**
 * Represents an association where a model A's table contains a foreign key to another model B's
 * [ApplicationRecord] object, in a "A belongs to B" fashion.
 */
interface BelongsToAssociation<T : ApplicationRecord> {
  val value: T?
}

abstract class BelongsToAssociationImpl<T : ApplicationRecord> : BelongsToAssociation<T> {
  override fun equals(other: Any?) =
      if (other is BelongsToAssociation<*>) {
        value == other.value
      } else {
        false
      }

  override fun hashCode() = value.hashCode()
}
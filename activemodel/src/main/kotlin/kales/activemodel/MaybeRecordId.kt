package kales.activemodel

sealed class MaybeRecordId
// ID for records that are only in-memory and not persisted in the database yet (or at all)
// Since `ApplicationRecord` IDs are generated (auto-increment), they should not be set when
// instantiating an object and instead auto assigned when persisting it to the database.
object NoneId : MaybeRecordId()

data class RecordId(val value: Int) : MaybeRecordId() {
  override fun toString() = value.toString()
}
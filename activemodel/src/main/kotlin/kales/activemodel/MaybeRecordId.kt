package kales.activemodel

import kales.ApplicationRecord

/**
 * Data type that represents [ApplicationRecord.id]. Can be either [NoneId] or [RecordId] to
 * represent the fact that an ID may or may not exist depending on whether the record has been
 * previously persisted to the DB or not.
 */
sealed class MaybeRecordId

/**
 * ID for records that are only in-memory and not persisted in the database yet (or at all)
 * Since `ApplicationRecord` IDs are generated (auto-increment), they should not be set when
 * instantiating an object and instead auto assigned when persisting it to the database.
 **/
object NoneId : MaybeRecordId()

/**
 * ID for records that have been previously persisted to the database and have an auto generated
 * value.
 */
data class RecordId(val value: Int) : MaybeRecordId() {
  override fun toString() = value.toString()
}
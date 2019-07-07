package kales.internal

import kales.ApplicationRecord
import kales.activemodel.BelongsToAssociationImpl
import kales.activemodel.use
import kotlin.reflect.KClass

class LazyBelongsToAssociation(
  private val toKlass: KClass<ApplicationRecord>,
  private val toRecordId: Int
) : BelongsToAssociationImpl<ApplicationRecord>() {
  override var value: ApplicationRecord? by mutableLazy {
    ApplicationRecord.JDBI.use {
      val queryBuilder = RecordQueryBuilder(it, KApplicationRecordClass(toKlass))
      queryBuilder.findRecord(toRecordId)
        .mapTo(toKlass.java)
        .findFirst()
        .orElse(null)
    }
  }
}
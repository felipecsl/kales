package kales.internal

import kales.ApplicationRecord
import kales.activemodel.BelongsToAssociationImpl
import kales.activemodel.use
import kotlin.reflect.KClass

class LazyBelongsToAssociation(
    private val toKlass: KClass<ApplicationRecord>,
    private val toRecordId: Int
) : BelongsToAssociationImpl<ApplicationRecord>() {
  override val value: ApplicationRecord? by lazy {
      ApplicationRecord.JDBI.use {
        val queryBuilder = RecordQueryBuilder(it, toKlass)
        queryBuilder.findRecord(toRecordId)
            .mapTo(toKlass.java)
            .findFirst()
            .orElse(null)
      }
    }
}
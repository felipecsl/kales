package kales.internal

import kales.ApplicationRecord
import kales.activemodel.HasManyAssociationImpl
import kales.activemodel.use
import kotlin.reflect.KClass

class LazyHasManyAssociation(
    override val fromKlass: KClass<ApplicationRecord>,
    private val toKlass: KClass<ApplicationRecord>,
    private val fromModelId: Int
) : HasManyAssociationImpl<ApplicationRecord, ApplicationRecord>(fromKlass) {
  override val collection: MutableList<ApplicationRecord>
      by lazy {
        ApplicationRecord.JDBI.use {
          val queryBuilder = RecordQueryBuilder(it, KApplicationRecordClass(toKlass))
          // We're assuming the property name matches the class name - That should always be
          // the case, e.: For table `Posts`, foreign key is `post_id`
          val clause = mapOf("${fromKlass.simpleName!!.toLowerCase()}_id" to fromModelId)
          queryBuilder.where(clause)
              .mapTo(toKlass.java)
              .list()
        }
      }
}
package kales

import kales.activemodel.CollectionModelAssociationImpl
import kales.activemodel.use
import kales.internal.RecordQueryBuilder
import kotlin.reflect.KClass

class LazyCollectionModelAssociation(
    override val fromKlass: KClass<ApplicationRecord>,
    private val toKlass: KClass<ApplicationRecord>,
    private val fromModelId: Int
) : CollectionModelAssociationImpl<ApplicationRecord, ApplicationRecord>(fromKlass) {
  override val collection: List<ApplicationRecord>
      by lazy {
        ApplicationRecord.JDBI.use {
          val queryBuilder = RecordQueryBuilder(it, toKlass)
          // We're assuming the property name matches the class name - That should always be
          // the case, e.: For table `Posts`, foreign key is `post_id`
          val clause = mapOf("${fromKlass.simpleName!!.toLowerCase()}_id" to fromModelId)
          queryBuilder.where(clause)
              .mapTo(toKlass.java)
              .list()
        }
      }
}
package kales

import kales.ApplicationRecord.Companion.allRecords
import kales.ApplicationRecord.Companion.createRecord
import kales.ApplicationRecord.Companion.findRecord
import kales.ApplicationRecord.Companion.saveRecord
import kales.ApplicationRecord.Companion.whereRecords
import kales.ApplicationRecord.Companion.destroyRecord
import kales.activemodel.*

data class Foo(
    override val id: MaybeRecordId = NoneId,
    val foo: String,
    val testModel: BelongsToAssociation<TestModel> = BelongsToAssociation.empty()
) : ApplicationRecord {
  fun save() = saveRecord()

  companion object {
    fun all() = allRecords<Foo>()

    fun where(id: Int? = null, foo: String? = null, testModel: Foo?) =
        whereRecords<Foo>(mapOf("id" to id, "foo" to foo, "testmodel_id" to testModel?.id))

    fun create(foo: String, testModel: TestModel? = null) =
        createRecord<Foo>(mapOf("foo" to foo, "testmodel_id" to testModel?.id))

    fun find(id: Int) = findRecord<Foo>(id)
    fun find(id: RecordId) = findRecord<Foo>(id)
  }
}

data class TestModel(
    override val id: MaybeRecordId = NoneId,
    val name: String,
    val foos: HasManyAssociation<TestModel, Foo> = HasManyAssociation.empty()
) : ApplicationRecord {
  fun save() = saveRecord()

  fun destroy() = destroyRecord()

  companion object {
    fun all() = allRecords<TestModel>()

    fun where(id: Int? = null, name: String? = null) =
        whereRecords<TestModel>(mapOf("id" to id, "name" to name))

    fun create(name: String) = createRecord<TestModel>(mapOf("name" to name))

    fun find(id: Int) = findRecord<TestModel>(id)
    fun find(id: RecordId) = findRecord<TestModel>(id)
  }
}
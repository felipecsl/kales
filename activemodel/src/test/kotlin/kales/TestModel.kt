package kales

import kales.activemodel.HasManyAssociation
import kales.activemodel.HasManyAssociation.Companion.empty
import kales.activemodel.BelongsToAssociation

data class Foo(
    val id: Int,
    val foo: String,
    val testModel: BelongsToAssociation<TestModel>
) : ApplicationRecord() {
  companion object {
    fun all() = allRecords<Foo>()

    fun where(id: Int? = null, foo: String? = null, testModel: Foo?) =
        whereRecords<Foo>(mapOf("id" to id, "foo" to foo, "testmodel_id" to testModel?.id))

    fun create(foo: String, testModel: TestModel) =
        createRecord<Foo>(mapOf("foo" to foo, "testmodel_id" to testModel.id))

    fun find(id: Int) = findRecord<Foo>(id)
  }
}

data class TestModel(
    val id: Int,
    val name: String,
    val foos: HasManyAssociation<TestModel, Foo> = empty()
) : ApplicationRecord() {
  companion object {
    fun all() = allRecords<TestModel>()

    fun where(id: Int? = null, name: String? = null) =
        whereRecords<TestModel>(mapOf("id" to id, "name" to name))

    fun create(name: String) = createRecord<TestModel>(mapOf("name" to name))

    fun find(id: Int) = findRecord<TestModel>(id)
  }
}
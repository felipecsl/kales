package kales

import kales.activemodel.ModelCollectionAssociation
import kales.activemodel.SingleModelAssociation

data class Foo(
    val id: Int,
    val foo: String
) : ApplicationRecord() {
  lateinit var testModel: SingleModelAssociation<TestModel>

  companion object {
    fun all() = allRecords<Foo>()

    fun where(vararg clause: Pair<String, Any>) = whereRecords<Foo>(clause.toMap())

    fun create(vararg values: Pair<String, Any>) = createRecord<Foo>(values.toMap())

    fun find(id: Int) = findRecord<Foo>(id)
  }
}

data class TestModel(
    val id: Int,
    val name: String
) : ApplicationRecord() {
  lateinit var foos: ModelCollectionAssociation<TestModel, Foo>

  companion object {
    fun all() = allRecords<TestModel>()

    fun where(vararg clause: Pair<String, Any>) = whereRecords<TestModel>(clause.toMap())

    fun create(vararg values: Pair<String, Any>) = createRecord<TestModel>(values.toMap())

    fun find(id: Int) = findRecord<TestModel>(id)
  }
}
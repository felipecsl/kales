package kales

data class TestModel(
    val id: Int,
    val name: String
) : ApplicationRecord() {
  companion object {
    fun all() = allRecords<TestModel>()

    fun where(vararg clause: Pair<String, Any>) = whereRecords<TestModel>(clause.toMap())

    fun create(vararg values: Pair<String, Any>) = createRecord<TestModel>(values.toMap())
  }
}
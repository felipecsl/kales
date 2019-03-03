package kales.sample.app.models

import kales.ApplicationRecord

data class Post(
    val id: Int,
    val title: String
) : ApplicationRecord() {
  companion object {
    fun all() = allRecords<Post>()

    fun where(vararg clause: Pair<String, Any>) = whereRecords<Post>(clause.toMap())

    fun find(id: Int) = findRecord<Post>(id)
  }
}
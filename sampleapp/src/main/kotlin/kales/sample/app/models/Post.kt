package kales.sample.app.models

import kales.ApplicationRecord
import kales.activemodel.CollectionModelAssociation

data class Post(
    val id: Int,
    val title: String,
    val content: String
) : ApplicationRecord() {
  lateinit var comments: CollectionModelAssociation<Post, Comment>

  companion object {
    fun all() = allRecords<Post>()

    fun create(title: String, content: String) =
        createRecord<Post>(mapOf("title" to title, "content" to content))

    fun where(vararg clause: Pair<String, Any>) = whereRecords<Post>(clause.toMap())

    fun find(id: Int) = findRecord<Post>(id)
  }
}
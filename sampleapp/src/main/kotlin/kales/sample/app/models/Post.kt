package kales.sample.app.models

import kales.ApplicationRecord
import kales.ApplicationRecord.Companion.allRecords
import kales.ApplicationRecord.Companion.createRecord
import kales.ApplicationRecord.Companion.findRecord
import kales.ApplicationRecord.Companion.whereRecords
import kales.ApplicationRecord.Companion.destroyRecord
import kales.activemodel.HasManyAssociation
import kales.activemodel.HasManyAssociation.Companion.empty

data class Post(
    override val id: Int,
    val title: String,
    val content: String,
    val comments: HasManyAssociation<Post, Comment> = empty()
) : ApplicationRecord {
  fun destroy() = destroyRecord()

  companion object {
    fun all() = allRecords<Post>()

    fun create(title: String, content: String) =
        createRecord<Post>(mapOf("title" to title, "content" to content))

    fun where(vararg clause: Pair<String, Any>) = whereRecords<Post>(clause.toMap())

    fun find(id: Int) = findRecord<Post>(id)
  }
}
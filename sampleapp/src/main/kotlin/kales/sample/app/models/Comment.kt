package kales.sample.app.models

import kales.ApplicationRecord
import kales.ApplicationRecord.Companion.allRecords
import kales.ApplicationRecord.Companion.findRecord

data class Comment(
    override val id: Int,
    val post: Post
) : ApplicationRecord {
  companion object {
    fun all() = allRecords<Comment>()

    fun find(id: Int) = findRecord<Comment>(id)
  }
}

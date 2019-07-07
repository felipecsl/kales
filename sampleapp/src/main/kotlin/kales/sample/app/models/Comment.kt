package kales.sample.app.models

import kales.ApplicationRecord
import kales.ApplicationRecord.Companion.allRecords
import kales.ApplicationRecord.Companion.createRecord
import kales.ApplicationRecord.Companion.findRecord
import kales.activemodel.BelongsToAssociation
import kales.activemodel.MaybeRecordId
import kales.activemodel.NoneId

data class Comment(
    override val id: MaybeRecordId = NoneId,
    val comment_text: String, // TODO we should automatically "camelize" the parameters
    val post: BelongsToAssociation<Post>? = null
) : ApplicationRecord {
  companion object {
    fun create(commentText: String, postId: Int) =
        createRecord<Comment>(mapOf("comment_text" to commentText, "post_id" to postId))

    fun all() = allRecords<Comment>()

    fun find(id: Int) = findRecord<Comment>(id)
  }
}

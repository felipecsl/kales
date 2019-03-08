package kales.sample.app.models

import kales.ApplicationRecord

data class Comment(val id: Int) : ApplicationRecord() {
  companion object {
    fun all() = allRecords<Comment>()

    fun find(id: Int) = findRecord<Comment>(id)
  }
}

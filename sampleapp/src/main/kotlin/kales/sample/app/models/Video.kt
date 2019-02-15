package kales.sample.app.models

import kales.ApplicationRecord
import kales.KalesApplication

data class Video(
    val id: Int,
    val title: String
) : ApplicationRecord() {
  companion object {
    fun all(): List<Video> {
      return ApplicationRecord.all(KalesApplication.INSTANCE.jdbi)
    }
  }
}
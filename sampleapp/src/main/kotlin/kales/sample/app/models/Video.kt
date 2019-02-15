package kales.sample.app.models

import kales.ApplicationRecord

data class Video(
    val id: Int,
    val title: String
) : ApplicationRecord() {
  companion object {
    fun all() = allRecords<Video>()
  }
}
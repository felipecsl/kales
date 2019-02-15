package kales

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo

abstract class ApplicationRecord {
  companion object {
    inline fun <reified T : ApplicationRecord> all(jdbi: Jdbi): List<T> {
      jdbi.open().use {
        return it.createQuery("select * from videos").mapTo<T>().list()
      }
    }
  }
}
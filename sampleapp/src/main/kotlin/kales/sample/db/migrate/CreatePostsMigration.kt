package kales.sample.db.migrate

import com.improve_future.harmonica.core.AbstractMigration

class CreatePostsMigration : AbstractMigration() {
  override fun up() {
    createTable("posts") {
      varchar(columnName = "title", nullable = false)
      text("content", nullable = true)
    }
  }

  override fun down() {
    dropTable("posts")
  }
}
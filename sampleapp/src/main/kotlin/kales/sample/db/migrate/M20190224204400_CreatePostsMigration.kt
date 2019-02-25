package kales.sample.db.migrate

import kales.migrations.Migration

class M20190224204400_CreatePostsMigration : Migration() {
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
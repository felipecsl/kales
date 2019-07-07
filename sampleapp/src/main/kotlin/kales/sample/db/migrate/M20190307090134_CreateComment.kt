package kales.sample.db.migrate

import kales.migrations.Migration

class M20190307090134_CreateComment : Migration() {
  override fun up() {
    createTable("comments") {
      integer(columnName = "post_id", nullable = false)
      text(columnName = "comment_text", nullable = false)
    }
  }

  override fun down() {
    dropTable("comments")
  }
}

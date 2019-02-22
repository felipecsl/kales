package kales.migrations

import java.sql.Connection
import java.sql.Statement

internal interface ActiveRecordConnection {
  val config: DbConfig
  fun transaction(block: Connection.() -> Unit)
  fun execute(sql: String): Boolean
  fun doesTableExist(tableName: String): Boolean
  fun createStatement(): Statement
}
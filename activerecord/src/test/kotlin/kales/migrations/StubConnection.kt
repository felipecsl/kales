package kales.migrations

import java.sql.Connection
import java.sql.Statement

class StubConnection : ActiveRecordConnection {
  override val config = DbConfig()

  val executedSqlList = mutableListOf<String>()

  override fun transaction(block: Connection.() -> Unit) {

  }

  override fun execute(sql: String): Boolean {
    executedSqlList.add(sql)
    return true
  }

  override fun doesTableExist(tableName: String): Boolean {
    return true
  }

  @Deprecated(
      "Cause Error",
      ReplaceWith("Can't be replaced. It's created only to meet interface.")
  )
  override fun createStatement(): Statement {
    return Statement::class.java.newInstance()
  }
}
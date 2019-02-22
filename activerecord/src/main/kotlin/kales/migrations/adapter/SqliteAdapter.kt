package kales.migrations.adapter

import kales.migrations.AbstractColumn
import kales.migrations.ActiveRecordConnection
import kales.migrations.IndexMethod
import kales.migrations.TableBuilder
import kales.migrations.column.AddingColumnOption

internal class SqliteAdapter(connection: ActiveRecordConnection) : DbAdapter(connection) {
  override fun createTable(tableName: String, tableBuilder: TableBuilder) {
    var sql = "CREATE TABLE $tableName (\n"
    if (tableBuilder.id) {
      sql += "  id INTEGER PRIMARY KEY AUTOINCREMENT"
      if (tableBuilder.columnList.size > 0) sql += ','
      sql += "\n"
    }
    sql += tableBuilder.columnList.joinToString(",\n") {
      "  " + buildColumnDeclarationForCreateTableSql(it)
    }
    sql += "\n);"
    connection.execute(sql)
  }

  override fun createIndex(
      tableName: String, columnNameArray: Array<String>, unique: Boolean,
      method: IndexMethod?
  ) {
    var sql = "CREATE "
    if (unique) sql += "UNIQUE "
    sql += "INDEX ${tableName}_${columnNameArray.joinToString("_")}_idx"
    sql += " ON $tableName (${columnNameArray.joinToString(",")});"
    connection.execute(sql)
  }

  override fun dropIndex(tableName: String, indexName: String) {
    val sql = "DROP INDEX $indexName;"
    connection.execute(sql)
  }

  override fun addColumn(
      tableName: String,
      column: AbstractColumn,
      option: AddingColumnOption
  ) {
    var sql = "ALTER TABLE $tableName ADD COLUMN "
    sql += buildColumnDeclarationForCreateTableSql(column)
    sql += ";"
    connection.execute(sql)
  }

  override fun renameTable(oldTableName: String, newTableName: String) {
    val sql = "ALTER TABLE $oldTableName RENAME TO $newTableName;"
    connection.execute(sql)
  }

  override fun renameIndex(
      tableName: String,
      oldIndexName: String,
      newIndexName: String
  ) {
    // Sqlite must drop index and create new index
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun addForeignKey(
      tableName: String,
      columnName: String,
      referencedTableName: String,
      referencedColumnName: String
  ) {
    // Sqlite doesn't support add Foreign Key function.
    // Foreign key must be added on table creation.
    TODO("not implemented")
  }

  override fun dropForeignKey(
      tableName: String,
      columnName: String,
      keyName: String
  ) {
    TODO("not implemented")
  }

  internal companion object : DbAdapter.CompanionInterface() {
    private fun buildColumnDeclarationForCreateTableSql(
        column: AbstractColumn
    ): String {
      var sql = column.name + " " + sqlType(column)
      if (!column.nullable) sql += " NOT NULL"
      if (column.hasDefault) {
        sql += " DEFAULT " + column.sqlDefault
      }
      if (column.hasReference)
        sql += " REFERENCES ${column.referenceTable} (${column.referenceColumn})"
      return sql
    }

    override fun sqlIndexMethod(method: IndexMethod?): String? {
      return null
    }
  }
}
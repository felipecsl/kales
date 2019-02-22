package kales.migrations.adapter

import kales.migrations.AbstractColumn
import kales.migrations.ActiveRecordConnection
import kales.migrations.IndexMethod
import kales.migrations.TableBuilder
import kales.migrations.column.*

internal class MySqlAdapter(connection: ActiveRecordConnection) : DbAdapter(connection) {
  override fun createTable(tableName: String, tableBuilder: TableBuilder) {
    var sql = "CREATE TABLE $tableName (\n"
    if (tableBuilder.id) {
      sql += "  id INT UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY"
      if (tableBuilder.columnList.size > 0) sql += ','
      sql += "\n"
    }
    sql += tableBuilder.columnList.joinToString(",\n") {
      "  " + buildColumnDeclarationForCreateTableSql(it)
    }
    sql += "\n)"
    if (tableBuilder.comment != null)
      sql += "\ncomment='${tableBuilder.comment}'"
    sql += ";"
    connection.execute(sql)
  }

  internal companion object : DbAdapter.CompanionInterface() {
    private fun buildColumnDeclarationForCreateTableSql(
        column: AbstractColumn
    ): String {
      var sql = column.name + " " + sqlType(column)
      when (column) {
        is VarcharColumn -> {
          sql +=
              if (column.size == null) "(255)"
              else "(" + column.size + ")"
        }
        is DecimalColumn -> {
          if (column.precision != null) {
            sql += "(" + column.precision.toString()
            if (column.scale != null) {
              sql += ", " + column.scale.toString()
            }
            sql += ")"
          }
        }
        is IntegerColumn -> {
          if (column.unsigned) {
            sql += " UNSIGNED"
          }
        }
      }
      if (!column.nullable) sql += " NOT NULL"
      if (column.hasDefault) {
        when (column) {
          is TextColumn, is BlobColumn -> Unit
          else -> {
            sql += " DEFAULT " + column.sqlDefault
          }
        }
      }
      if (column.hasReference)
        sql += " REFERENCES ${column.referenceTable} (${column.referenceColumn})"
      if (column.hasComment)
        sql += " COMMENT '${column.comment}'"
      return sql
    }

    override fun sqlIndexMethod(method: IndexMethod?): String? {
      return when (method) {
        IndexMethod.BTree -> "BTREE"
        IndexMethod.Hash -> "HASH"
        else -> null
      }
    }
  }

  override fun createIndex(
      tableName: String, columnNameArray: Array<String>, unique: Boolean,
      method: IndexMethod?
  ) {
    var sql = "CREATE"
    if (unique) sql += " UNIQUE"
    sql += " INDEX ${tableName}_${columnNameArray.joinToString("_")}_idx"
    sqlIndexMethod(method)?.let { sql += " $it" }
    sql += " ON $tableName (${columnNameArray.joinToString(", ")});"
    connection.execute(sql)
  }

  override fun dropIndex(tableName: String, indexName: String) {
    connection.execute("DROP INDEX $indexName ON $tableName;")
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
    val sql = "RENAME TABLE $oldTableName TO $newTableName;"
    connection.execute(sql)
  }


  override fun renameIndex(
      tableName: String, oldIndexName: String, newIndexName: String
  ) {
    val sql = "ALTER TABLE $tableName" +
        " RENAME INDEX $oldIndexName TO $newIndexName"
    connection.execute(sql)
  }

  override fun addForeignKey(
      tableName: String, columnName: String,
      referencedTableName: String, referencedColumnName: String
  ) {
    val sql = "ALTER TABLE $tableName" +
        " ADD CONSTRAINT ${tableName}_${columnName}_fkey" +
        " FOREIGN KEY ($columnName) REFERENCES" +
        " $referencedTableName ($referencedColumnName);"
    connection.execute(sql)
  }

  override fun dropForeignKey(
      tableName: String,
      columnName: String,
      keyName: String
  ) {
    val sql = "ALTER TABLE $tableName" +
        " DROP FOREIGN KEY $keyName;"
    connection.execute(sql)
  }
}
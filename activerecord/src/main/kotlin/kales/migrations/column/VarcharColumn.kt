package kales.migrations.column

import kales.migrations.AbstractColumn

internal class VarcharColumn(name: String) : AbstractColumn(name) {
  var size: Int? = null

  var default: String? = null
    set(value) {
      field = value
      sqlDefault = value?.let { "'$it'" }
    }

  override var sqlDefault: String? = null
}
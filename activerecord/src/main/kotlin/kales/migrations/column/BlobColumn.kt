package kales.migrations.column

import kales.migrations.AbstractColumn
import kales.migrations.toHexString

internal class BlobColumn(name: String) : AbstractColumn(name) {
  var default: ByteArray? = null
    set(value) {
      field = value
      sqlDefault = value?.let { "E'\\\\x" + it.toHexString() + "'" }
    }

  override var sqlDefault: String? = null
}
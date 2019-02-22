package kales.migrations

fun ByteArray.toHexString() : String {
  return joinToString("") {
    java.lang.String.format("%02x", it)
  }
}
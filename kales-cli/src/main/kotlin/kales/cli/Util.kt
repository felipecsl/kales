package kales.cli

import java.io.File

/** Like list() but returns empty list instead of null */
fun File.safeListFiles(): List<File> {
  return listFiles()?.asList() ?: emptyList()
}
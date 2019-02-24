package kales.cli

import com.github.ajalt.clikt.core.UsageError
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset

/** Like list() but returns empty list instead of null */
fun File.safeListFiles(): List<File> {
  return listFiles()?.asList() ?: emptyList()
}

/** Print messages about file changes when writing text to it */
fun File.writeTextWithLogging(text: String, charset: Charset = Charsets.UTF_8) {
  if (exists()) {
    if (readText(charset) != text) {
      printConflict()
      // TODO add the option for the user to abort, overwrite or merge with current file
      throw UsageError("Failed to write file since it already exists with different contents")
    } else {
      // Files are identical
      printIdentical()
    }
  } else {
    printCreated()
    writeText(text, charset)
  }
}

fun File.printCreated() {
  printStatus("create")
}

fun File.printSkipped() {
  printStatus("skip")
}

fun File.printIdentical() {
  printStatus("identical")
}

fun File.printConflict() {
  printStatus("conflict")
}

fun File.printStatus(status: String) {
  val relativePath = relativePathToWorkingDir()
  println("   $status $relativePath")
}

/** Copy streams and close at the end */
fun InputStream.copyToWithLogging(destination: File): Long {
  if (!destination.exists() || destination.length() == 0L) {
    destination.printCreated()
  } else {
    destination.printSkipped()
  }
  use { input ->
    destination.outputStream().use { output ->
      return input.copyTo(output)
    }
  }
}

/** Returns a String with the path from this File relative to the current working dir (user.dir) */
fun File.relativePathToWorkingDir(): String {
  val workingDir = File(System.getProperty("user.dir"))
  return workingDir.toPath().relativize(absoluteFile.toPath()).toString()
}

/** Returns a relative path String for a set of directory names */
fun relativePathFor(vararg pathSegments: String) =
    pathSegments.toSet().joinToString(File.separator)
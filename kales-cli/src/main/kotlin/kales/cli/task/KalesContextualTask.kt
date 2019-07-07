package kales.cli.task

import com.github.ajalt.clikt.core.UsageError
import com.squareup.kotlinpoet.FileSpec
import kales.cli.relativePathFor
import kales.cli.safeListFiles
import kales.cli.writeTextWithLogging
import kales.migrations.KalesDatabaseConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Path

/**
 * Defines a Kales task that is supposed to run from an existing Kales application root directory,
 * defined by [applicationRootDir], eg.: "~/projects/foo"
 */
abstract class KalesContextualTask(protected val applicationRootDir: File) : KalesTask {
  private val kotlinDir = File(applicationRootDir, relativePathFor("src", "main", "kotlin"))

  protected val resourcesDir = File(applicationRootDir, relativePathFor("src", "main", "resources"))

  /** returns the directory where the code for package "com.example.foo.app" is found */
  protected val appDirectory = findAppDirectory()
    ?: throw UsageError("Unable to find the `app` sources directory")

  /** Returns the path to the migrations directory */
  protected val dbMigrateDir = File(appDirectory.parentFile, relativePathFor("db", "migrate"))

  /** returns "com.example.foo" */
  protected val appPackageName =
    recursivelyDetermineAppPackageName(appDirectory, appDirectory)
      ?.split("/")
      ?.joinToString(".")

  /** Returns a File pointing to the application app/`type` directory or null if none found */
  private fun findAppDirectory(): File? {
    return kotlinDir.childDirectories()
      .mapNotNull(this::recursivelyFindAppDirectory)
      .firstOrNull()
  }

  private fun recursivelyFindAppDirectory(currentDir: File): File? {
    val childDirs = currentDir.childDirectories()
    return if (currentDir.name == "app" &&
      childDirs.map { it.name }.containsAll(setOf("controllers", "views", "models"))) {
      currentDir
    } else {
      childDirs.mapNotNull(this::recursivelyFindAppDirectory).firstOrNull()
    }
  }

  private fun recursivelyDetermineAppPackageName(currentDir: File, appDir: File): String? {
    return when {
      currentDir.name == "src" ->
        null
      currentDir.name == "kotlin" ->
        currentDir.toPath().relativize(appDir.parentFile.toPath()).toString()
      else ->
        recursivelyDetermineAppPackageName(currentDir.parentFile, appDir)
    }
  }

  /** Returns a [KalesDatabaseConfig] representing the current project's `database.yml` file */
  protected fun readDatabaseConfig(): KalesDatabaseConfig {
    val databaseYml = File(resourcesDir, "database.yml")
    if (!databaseYml.exists()) {
      throw UsageError("database.yml file not found.\n" +
        "Plase make sure it exists under src/main/resources and try again")
    }
    return KalesDatabaseConfig.fromDatabaseYml(databaseYml.inputStream())
  }

  /** Writes this [FileSpec] to the provided file [Path] */
  protected fun FileSpec.rawWriteTo(destination: Path) {
    ByteArrayOutputStream().use { baos ->
      OutputStreamWriter(baos, StandardCharsets.UTF_8).use { writer ->
        writeTo(writer)
      }
      destination.toFile().writeTextWithLogging(baos.toString())
    }
  }

  private fun File.childDirectories() =
    safeListFiles().filter { it.isDirectory }
}

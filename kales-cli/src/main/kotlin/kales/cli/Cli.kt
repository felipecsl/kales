package kales.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import kales.cli.task.*
import java.io.File

class Cli : NoRunCliktCommand() {
  override fun aliases() =
      mapOf(
          "g" to listOf("generate"),
          "v" to listOf("version"),
          "n" to listOf("new")
      )
}

class Version : CliktCommand(help = """
  Displays the Kales version
""".trimIndent()) {
  override fun run() {
    KalesVersionTask().run()
  }
}

class New : CliktCommand(help = """
  Creates a new Kales application

  The 'kales new' command creates a new Kales application with a default
    directory structure and configuration at the path you specify.
  """.trimIndent()) {

  private val appName by argument(help = """
    the application name in reverse domain name notation, eg.: \"com.example.foo\""
  """.trimIndent())

  override fun run() {
    NewApplicationTask(workingDir(), appName).run()
  }
}

class Generate : NoRunCliktCommand(help = """
  Runs a Kales generator
""".trimIndent())

class DbMigrate : CliktCommand(name = "db:migrate", help = """
  Migrate the database
""".trimIndent()) {
  override fun run() {
    DbMigrateTask(workingDir()).run()
  }
}

// TODO: Add support for different KALES_ENV (development, production, test, etc)
class DbCreate : CliktCommand(name = "db:create", help = """
  Creates the database from resources/database.yml
""".trimIndent()) {
  override fun run() {
    DbCreateTask(workingDir()).run()
  }
}

class GenerateController : CliktCommand(name = "controller", help = """
    Stubs out a new controller. Pass the CamelCased controller name.

    This generates a controller class in app/controllers.
""".trimIndent()) {
  private val name by argument()

  private val actions by argument().multiple()

  override fun run() {
    GenerateControllerTask(workingDir(), name, actions.toSet()).run()
  }
}

class GenerateModel : CliktCommand(name = "model", help = """
    Stubs out a new model. Pass the CamelCased model name.

    This generates a model class in app/models and an accompanying
    migration to create the database table
""".trimIndent()) {
  private val modelName by argument()

  override fun run() {
    GenerateModelTask(workingDir(), modelName).run()
  }
}

class GenerateView : CliktCommand(name = "view", help = """
    Stubs out a new view. Pass the CamelCased view name, eg.: IndexView.

    This generates a view class in app/views/<resource>.
""".trimIndent()) {
  private val viewName by argument()
  private val resourceName by argument()

  override fun run() {
    GenerateViewTask(workingDir(), resourceName, viewName).run()
  }
}

class GenerateMigration : CliktCommand(name = "migration", help = """
    Stubs out a new database migration. Pass the migration name CamelCased.

    A migration class is generated in db/migrate prefixed by a timestamp of the current date and time.
""".trimIndent()) {
  private val migrationName by argument()

  override fun run() {
    GenerateMigrationTask(workingDir(), migrationName).run()
  }
}

fun main(args: Array<String>) {
  val generateCommand = Generate()
      .subcommands(GenerateController())
      .subcommands(GenerateModel())
      .subcommands(GenerateView())
      .subcommands(GenerateMigration())
  Cli().subcommands(New(), generateCommand, DbMigrate(), DbCreate(), Version())
      .main(args)
}

fun workingDir() = File(System.getProperty("user.dir"))
package kales.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.io.File

class Cli : CliktCommand() {
  override fun run() = Unit
}

class New : CliktCommand(help = """
  Creates a new Kales application

  The 'kales new' command creates a new Kales application with a default
    directory structure and configuration at the path you specify.
  """.trimIndent()) {
  private val appPath by option(help = """
    the path to your new app directory, eg.: ~/Code/Kotlin/weblog
  """.trimIndent()).required()
  private val appName by option(help = """
    the application name in reverse domain name notation, eg.: \"com.example.foo\""
  """.trimIndent()).required()

  override fun run() {
    NewCommandRunner(appPath, appName).run()
  }
}

class Generate : CliktCommand(help = """
  Runs a Kales generator
""".trimIndent()) {

  override fun run() = Unit
}

class GenerateController : CliktCommand(name = "controller", help = """
    Stubs out a new controller. Pass the CamelCased controller name.

    This generates a controller class in app/controllers.
""".trimIndent()) {
  private val name by option().required()

  override fun run() {
    val workingDir = File(System.getProperty("user.dir"))
    GenerateControllerCommandRunner(workingDir, name).run()
  }
}

fun main(args: Array<String>) = Cli()
    .subcommands(New(), Generate().subcommands(GenerateController()))
    .main(args)
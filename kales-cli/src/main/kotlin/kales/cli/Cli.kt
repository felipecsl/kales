package kales.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

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

fun main(args: Array<String>) = Cli().subcommands(New()).main(args)
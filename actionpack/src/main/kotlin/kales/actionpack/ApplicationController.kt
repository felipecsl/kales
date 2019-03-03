package kales.actionpack

import io.ktor.application.ApplicationCall
import kales.actionview.ViewModel

abstract class ApplicationController(val call: ApplicationCall) {
  var bindings: ViewModel? = null
}
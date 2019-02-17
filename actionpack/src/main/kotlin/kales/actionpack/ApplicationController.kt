package kales.actionpack

import io.ktor.application.ApplicationCall
import kales.actionview.ViewModel

abstract class ApplicationController(val call: ApplicationCall) {
  var bindings: ViewModel? = null

  open fun index(): Any? = null

  open fun show(): Any? = null

  open fun create(): Any? = null

  open fun new(): Any? = null

  companion object {
    fun newInstance() {
    }
  }
}
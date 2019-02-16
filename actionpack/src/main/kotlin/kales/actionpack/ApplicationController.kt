package kales.actionpack

import io.ktor.application.ApplicationCall
import kales.actionview.ActionView

abstract class ApplicationController(val call: ApplicationCall) {
  open fun index(): ActionView<*>? = null

  open fun show(): ActionView<*>? = null

  open fun create(): ActionView<*>? = null

  open fun new(): ActionView<*>? = null

  companion object {
    fun newInstance() {
    }
  }
}
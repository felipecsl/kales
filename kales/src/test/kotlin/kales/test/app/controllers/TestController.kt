package kales.test.app.controllers

import io.ktor.application.ApplicationCall
import kales.actionpack.ApplicationController
import kales.test.app.views.test.IndexViewModel

class TestController(call: ApplicationCall) : ApplicationController(call) {
  fun index(): Any? {
    bindings = IndexViewModel("foo")
    return null
  }
}
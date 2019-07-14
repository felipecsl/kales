package kales.test.app.controllers

import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import kales.actionpack.ApplicationController
import kales.test.app.views.test.CreateViewModel
import kales.test.app.views.test.IndexView
import kales.test.app.views.test.IndexViewModel

@Suppress("unused")
class TestController(call: ApplicationCall) : ApplicationController(call) {
  fun index() {
    bindings = IndexViewModel("foo")
  }

  suspend fun create() {
    val params = call.receiveParameters()
    bindings = CreateViewModel(params["message"]!!)
  }

  fun destroy(): IndexView {
    bindings = IndexViewModel("from destroy")
    return IndexView(bindings as IndexViewModel)
  }

  fun put(): IndexView {
    bindings = IndexViewModel("putting")
    return IndexView(bindings as IndexViewModel)
  }

  fun patch(): IndexView {
    bindings = IndexViewModel("patchin'")
    return IndexView(bindings as IndexViewModel)
  }
}
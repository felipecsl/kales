package kales.test.app.controllers

import io.ktor.application.ApplicationCall
import kales.actionpack.ApplicationController
import kales.test.app.views.test.CreateViewModel
import kales.test.app.views.test.IndexView
import kales.test.app.views.test.IndexViewModel

@Suppress("unused")
class TestController(call: ApplicationCall) : ApplicationController(call) {
  fun index() {
    bindings = IndexViewModel("foo")
  }

  fun actionWithoutView() {
  }

  suspend fun create() {
    val params = receiveParameters()
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

  suspend fun patch(): IndexView {
    val params = receiveParameters()
    bindings = IndexViewModel("patchin' ${params["foo"]}")
    return IndexView(bindings as IndexViewModel)
  }
}
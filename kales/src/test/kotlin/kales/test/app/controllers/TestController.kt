package kales.test.app.controllers

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpMethod.Companion.Post
import io.ktor.request.receiveParameters
import kales.actionpack.ApplicationController
import kales.test.app.views.test.CreateViewModel
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
}
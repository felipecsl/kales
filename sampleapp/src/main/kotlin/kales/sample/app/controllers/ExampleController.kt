package kales.sample.app.controllers

import io.ktor.application.ApplicationCall
import kales.actionpack.ApplicationController
import kales.sample.app.models.Video
import kales.sample.app.views.example.IndexViewModel
import kales.sample.app.views.example.ShowViewModel

class ExampleController(call: ApplicationCall) : ApplicationController(call) {
  override fun index(): Any? {
    bindings = IndexViewModel("Felipe", Video.all())
    return null
  }

  override fun show(): Any? {
    bindings = ShowViewModel(Video.find(call.parameters["id"]?.toInt()
        ?: throw IllegalArgumentException("Missing parameter id")))
    return null
  }
}
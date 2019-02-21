package kales.sample.app.controllers

import io.ktor.application.ApplicationCall
import kales.actionpack.ApplicationController
import kales.sample.app.models.Video
import kales.sample.app.views.example.IndexView
import kales.sample.app.views.example.IndexViewModel

class ExampleController(call: ApplicationCall) : ApplicationController(call) {
  override fun index(): Any? {
    bindings = IndexViewModel("Felipe", Video.all())
    return null
  }

  override fun show(): IndexView {
    val id = call.parameters["id"] ?: throw RuntimeException("Missing parameter ID")
    return IndexView(IndexViewModel(id, Video.where("id" to id.toInt())))
  }
}
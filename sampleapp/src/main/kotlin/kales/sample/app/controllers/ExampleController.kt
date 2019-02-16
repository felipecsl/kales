package kales.sample.app.controllers

import io.ktor.application.ApplicationCall
import kales.actionpack.ApplicationController
import kales.sample.app.models.Video
import kales.sample.app.views.example.ExampleIndexView
import kales.sample.app.views.example.ExampleIndexViewModel

class ExampleController(call: ApplicationCall) : ApplicationController(call) {
  override fun index() =
      ExampleIndexView(ExampleIndexViewModel("Felipe", Video.all()))

  override fun show() =
      ExampleIndexView(ExampleIndexViewModel(call.parameters["id"] ?: "?", listOf()))
}
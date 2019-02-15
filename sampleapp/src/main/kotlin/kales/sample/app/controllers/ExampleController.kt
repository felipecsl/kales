package kales.sample.app.controllers

import kales.actionpack.ApplicationController
import kales.sample.app.models.Video
import kales.sample.app.views.example.ExampleIndexView
import kales.sample.app.views.example.ExampleIndexViewModel

class ExampleController : ApplicationController() {
  @Suppress("UNCHECKED_CAST")
  override fun index(): ExampleIndexView {
    return ExampleIndexView(ExampleIndexViewModel("Felipe", Video.all()))
  }

  fun foo() =
      ExampleIndexView(ExampleIndexViewModel("Foo", listOf()))
}
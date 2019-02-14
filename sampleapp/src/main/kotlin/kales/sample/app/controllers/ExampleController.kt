package kales.sample.app.controllers

import kales.actionpack.ApplicationController
import kales.actionview.ActionView
import kales.sample.app.views.example.ExampleIndexView
import kales.sample.app.views.example.ExampleIndexViewModel

class ExampleController : ApplicationController() {
  @Suppress("UNCHECKED_CAST")
  override fun index(): ActionView<*>? {
    val bindings = ExampleIndexViewModel("Felipe")
    return ExampleIndexView(bindings)
  }

  fun foo(): ActionView<*>? {
    val bindings = ExampleIndexViewModel("Foo")
    return ExampleIndexView(bindings)
  }
}
package kales.sample.app.controllers

import kales.actionpack.ApplicationController
import kales.sample.app.views.example.ExampleIndexView
import kales.sample.app.views.example.ExampleIndexViewModel
import kotlinx.html.HTML

class ExampleController : ApplicationController() {
  @Suppress("UNCHECKED_CAST")
  override fun index() = { html: HTML ->
    val bindings = ExampleIndexViewModel("Felipe")
    ExampleIndexView(html).render(bindings)
  }
}
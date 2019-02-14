package kales.sample.app.views.example

import kales.actionview.ActionView
import kotlinx.html.FlowContent
import kotlinx.html.h2
import kotlinx.html.p

class ExampleIndexView(
    bindings: ExampleIndexViewModel?
) : ActionView<ExampleIndexViewModel>(bindings) {
  override fun render(content: FlowContent) {
    content.h2 {
      +"Hello, ${bindings?.name}"
    }
    content.p {
      +"Greetings from Kales"
    }
  }
}
package kales.sample.app.views.example

import kotlinx.html.*
import kales.actionview.ActionView
import kales.actionview.ViewModel

class ExampleIndexView(html: HTML) : ActionView<ExampleIndexViewModel>(html) {
  override fun render(bindings: ExampleIndexViewModel?) {
    html.head {
      title { +"Kales sample app" }
    }
    html.body {
      h1 {
        +"Hello, ${bindings?.name}"
      }
      p {
        +"Greetings from Kales"
      }
    }
  }
}
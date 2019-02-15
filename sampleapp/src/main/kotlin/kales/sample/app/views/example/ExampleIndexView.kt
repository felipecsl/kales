package kales.sample.app.views.example

import kales.actionview.ActionView
import kotlinx.html.*

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
    content.h3 { +"Videos" }
    content.ul {
      bindings?.videos?.forEach { v ->
        li {
          +v.title
        }
      }
    }
  }
}
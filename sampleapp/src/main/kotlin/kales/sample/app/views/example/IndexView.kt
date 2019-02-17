package kales.sample.app.views.example

import kales.actionview.ActionView
import kotlinx.html.*

class IndexView(
    bindings: IndexViewModel? = IndexViewModel("Unknown", listOf())
) : ActionView<IndexViewModel>(bindings) {
  override fun render(content: FlowContent) {
    content.apply {
      h2 {
        +"Hello, ${bindings?.name}"
      }
      p {
        +"Greetings from Kales"
      }
      h3 {
        +"Videos"
      }
      ul {
        bindings?.videos?.forEach { v ->
          li {
            +v.title
          }
        }
      }
    }
  }
}
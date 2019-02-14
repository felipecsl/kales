package krails.sample.views.example

import kotlinx.html.*
import krails.actionview.ActionView

class ExampleIndexView(html: HTML) : ActionView(html) {
  override fun render() {
    html.head {
      title { +"Hello World" }
    }
    html.body {
      h1 {
        +"Title"
      }
    }
  }
}
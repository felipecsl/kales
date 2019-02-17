package kales.test.app.views.test

import kales.actionview.ActionView
import kotlinx.html.FlowContent
import kotlinx.html.h1

class IndexView(bindings: IndexViewModel) : ActionView<IndexViewModel>(bindings) {
  override fun render(content: FlowContent) {
    content.apply {
      h1 {
        +"Hello ${bindings?.greeting}"
      }
    }
  }
}
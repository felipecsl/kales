package kales.test.app.views.test

import kales.actionview.ActionView
import kotlinx.html.FlowContent
import kotlinx.html.h1

class IndexView(
  bindings: IndexViewModel? = IndexViewModel("Hello")
) : ActionView<IndexViewModel>(bindings) {
  override fun FlowContent.render() {
    h1 {
      +"Hello ${bindings?.greeting}"
    }
  }
}
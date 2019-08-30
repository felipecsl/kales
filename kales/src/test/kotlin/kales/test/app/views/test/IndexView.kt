package kales.test.app.views.test

import kales.actionpack.KalesApplicationCall
import kales.actionview.ActionView
import kotlinx.html.FlowContent
import kotlinx.html.h1

class IndexView(
  call: KalesApplicationCall,
  bindings: IndexViewModel? = IndexViewModel("Hello")
) : ActionView<IndexViewModel>(call, bindings) {
  override fun FlowContent.render() {
    h1 {
      +"Hello ${bindings?.greeting}"
    }
  }
}
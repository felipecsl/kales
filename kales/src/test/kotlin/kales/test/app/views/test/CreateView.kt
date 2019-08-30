package kales.test.app.views.test

import kales.actionpack.KalesApplicationCall
import kales.actionview.ActionView
import kotlinx.html.FlowContent
import kotlinx.html.h1

class CreateView(
  call: KalesApplicationCall,
  bindings: CreateViewModel? = CreateViewModel("")
) : ActionView<CreateViewModel>(call, bindings) {
  override fun FlowContent.render() {
    h1 {
      +"Posted: ${bindings?.message}"
    }
  }
}
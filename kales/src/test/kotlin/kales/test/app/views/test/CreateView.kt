package kales.test.app.views.test

import kales.actionview.ActionView
import kotlinx.html.FlowContent
import kotlinx.html.h1

class CreateView(
  bindings: CreateViewModel? = CreateViewModel("")
) : ActionView<CreateViewModel>(bindings) {
  override fun FlowContent.render() {
    h1 {
      +"Posted: ${bindings?.message}"
    }
  }
}
package kales.sample.app.views.posts

import kales.actionview.ActionView
import kotlinx.html.FlowContent
import kotlinx.html.h2
import kotlinx.html.h3

class ShowView(
    bindings: ShowViewModel? = ShowViewModel()
) : ActionView<ShowViewModel>(bindings) {
  override fun render(content: FlowContent) {
    content.apply {
      h2 { +"Details" }
      h3 {
        +"Post ${bindings?.post}"
      }
    }
  }
}
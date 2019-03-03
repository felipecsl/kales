package kales.sample.app.views.posts

import kales.actionview.ActionView
import kales.actionview.ViewModel
import kotlinx.html.*

class NewView(
    bindings: ViewModel? = null
) : ActionView<ViewModel>(bindings) {
  override fun render(content: FlowContent) {
    content.apply {
      h2 {
        +"New Post"
      }
    }
  }
}
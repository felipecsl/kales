package kales.sample.app.views.posts

import kales.actionview.ActionView
import kales.actionview.ViewModel
import kotlinx.html.*
import kotlinx.html.FormMethod.post
import kotlinx.html.InputType.text

class NewView(
    bindings: ViewModel? = null
) : ActionView<ViewModel>(bindings) {
  override fun FlowContent.render() {
    h2 {
      +"New Post"
    }
    form(action = "/posts", method = post) {
      div {
        label { +"Title" }
        input(type = text, name = "title")
      }
      div {
        label { +"Content" }
        input(type = text, name = "content")
      }
      submitInput(name = "Create")
    }
  }
}
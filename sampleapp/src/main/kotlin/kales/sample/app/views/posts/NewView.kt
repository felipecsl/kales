package kales.sample.app.views.posts

import kales.actionpack.KalesApplicationCall
import kales.actionpack.ViewModel
import kales.actionview.ActionView
import kotlinx.html.*
import kotlinx.html.FormMethod.post
import kotlinx.html.InputType.text
import kotlinx.html.attributes.enumEncode

class NewView(
  call: KalesApplicationCall,
  bindings: ViewModel? = null
) : ActionView<ViewModel>(call, bindings) {
  override fun FlowContent.render() {
    div("container") {
      div("row") {
        div("col-sm-8 offset-sm-2") {
          h2 {
            +"New Post"
          }
        }
      }
    }
    div("container") {
      div("row") {
        div("col-sm-8 offset-sm-2") {
          form(action = "/posts", method = post) {
            div("form-group") {
              label { +"Post title" }
              input(type = text, name = "title", classes = "form-control")
            }
            div("form-group") {
              label { +"Post content" }
              textArea(rows = "10", classes = "form-control", name = "content")
            }
            submitInput(name = "Create", classes = "btn btn-primary") {
              value = "Create"
            }
          }
        }
      }
    }
  }
}

@HtmlTagMarker
fun FlowOrInteractiveOrPhrasingContent.textArea(
  rows: String? = null,
  cols: String? = null,
  wrap: TextAreaWrap? = null,
  classes: String? = null,
  name: String? = null,
  block: TEXTAREA.() -> Unit = {}
): Unit =
    TEXTAREA(attributesMapOf(
        "rows", rows,
        "name", name,
        "cols", cols,
        "wrap", wrap?.enumEncode(),
        "class", classes), consumer).visit(block)
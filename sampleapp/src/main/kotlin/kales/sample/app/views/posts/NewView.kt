package kales.sample.app.views.posts

import kales.actionview.ActionView
import kales.actionview.ViewModel
import kotlinx.html.*
import kotlinx.html.FormMethod.post
import kotlinx.html.InputType.text
import kotlinx.html.attributes.enumEncode

class NewView(
    bindings: ViewModel? = null
) : ActionView<ViewModel>(bindings) {
  override fun FlowContent.render() {
    div("container") {
      div("row") {
        div("col-sm-12") {
          h2 {
            +"New Post"
          }
        }
      }
    }
    div("container") {
      div("row") {
        div("col-sm-6 offset-sm-3") {
          form(action = "/posts", method = post) {
            div("form-group") {
              label { +"Title" }
              input(type = text, name = "title", classes = "form-control")
            }
            div("form-group") {
              label { +"Content" }
              textArea(rows = "3", classes = "form-control", name = "content")
            }
            submitInput(name = "Create", classes = "btn btn-primary")
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
    block : TEXTAREA.() -> Unit = {}): Unit =
    TEXTAREA(attributesMapOf(
        "rows", rows,
        "name", name,
        "cols", cols,
        "wrap", wrap?.enumEncode(),
        "class", classes), consumer).visit(block)
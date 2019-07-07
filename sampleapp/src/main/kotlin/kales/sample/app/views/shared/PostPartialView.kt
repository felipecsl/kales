package kales.sample.app.views.shared

import kales.actionview.ActionPartialView
import kales.sample.app.views.posts.PostViewModel
import kales.sample.app.views.posts.textArea
import kotlinx.html.*

class PostPartialView(
  bindings: PostViewModel? = PostViewModel()
) : ActionPartialView<PostViewModel>(bindings) {
  override fun FlowContent.render() {
    div("container") {
      form(action = "/posts/${bindings?.post?.id}", method = FormMethod.post) {
        div("form-group") {
          div("col-sm-8 offset-sm-2") {
            label { +"Title" }
            textInput(classes = "form-control", name = "post[title]") {
              value = "${bindings?.post?.title}"
            }
          }
        }
        div("form-group") {
          div("col-sm-8 offset-sm-2") {
            label { +"Content" }
            textArea(rows = "5", classes = "form-control", name = "post[content]") {
              +"${bindings?.post?.content}"
            }
          }
        }
        div("form-group") {
          div("col-sm-8 offset-sm-2") {
            submitInput(name = "Save", classes = "btn btn-primary")
          }
        }
      }
      div("row") {
        div("col-sm-6 offset-sm-3") {
          h4 { +"Write a comment" }
        }
      }
      div("row") {
        div("col-sm-6 offset-sm-3") {
          form(action = "/posts/${bindings?.post?.id}/comments", method = FormMethod.post) {
            div("form-group") {
              label { +"Comment" }
              hiddenInput(name = "id") {
                value = bindings?.post?.id?.toString()!!
              }
              textArea(rows = "3", classes = "form-control", name = "comment_text")
            }
            submitInput(name = "OK", classes = "btn btn-primary")
          }
        }
      }
      div("row") {
        div("col-sm-6 offset-sm-3") {
          h4 { +"Comments " }
        }
      }
      bindings?.post?.comments?.forEach { comment ->
        div("row") {
          div("col-sm-6 offset-sm-3") {
            p { +comment.comment_text }
          }
        }
      }
    }
  }
}
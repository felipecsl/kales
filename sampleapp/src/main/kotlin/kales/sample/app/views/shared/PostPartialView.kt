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
      div("row") {
        div("col-sm-12") {
          h3 { +"${bindings?.post?.title}" }
        }
      }
      div("row") {
        div("col-sm-12") {
          p { +"${bindings?.post?.content}" }
        }
      }
      div("row") {
        div("col-sm-12") {
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
              textArea(rows = "3", classes = "form-control", content = "", name = "comment_text")
            }
            submitInput(name = "OK", classes = "btn btn-primary")
          }
        }
      }
      div("row") {
        div("col-sm-12") {
          h4 { +"Comments "}
        }
      }
      bindings?.post?.comments?.forEach { comment ->
        div("row") {
          div("col-sm-12") {
            p { +comment.comment_text }
          }
        }
      }
    }
  }
}
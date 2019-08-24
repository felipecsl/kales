package kales.sample.app.views.shared

import kales.actionview.ActionPartialView
import kales.actionview.KalesFormMethod
import kales.actionview.formFor
import kales.sample.app.views.posts.PostViewModel
import kales.sample.app.views.posts.textArea
import kotlinx.html.*

class PostPartialView(
  bindings: PostViewModel? = PostViewModel(),
  private val method: KalesFormMethod = KalesFormMethod.post
) : ActionPartialView<PostViewModel>(bindings) {
  override fun FlowContent.render() {
    div("container") {
      val post = bindings?.post
      formFor(post, method = method) {
        div("form-group") {
          div("col-sm-8 offset-sm-2") {
            label { +"Title" }
            /** TODO replace with form helpers (eg.: textFieldFor()) */
            textInput(classes = "form-control", name = "post[title]") {
              value = "${post?.title}"
            }
          }
        }
        div("form-group") {
          div("col-sm-8 offset-sm-2") {
            label { +"Content" }
            textArea(rows = "5", classes = "form-control", name = "post[content]") {
              +"${post?.content}"
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
          form(action = "/posts/${post?.id}/comments", method = FormMethod.post) {
            div("form-group") {
              label { +"Comment" }
              hiddenInput(name = "id") {
                value = post?.id?.toString()!!
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
      post?.comments?.forEach { comment ->
        div("row") {
          div("col-sm-6 offset-sm-3") {
            p { +comment.comment_text }
          }
        }
      }
    }
  }
}
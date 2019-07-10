package kales.sample.app.views.posts

import kales.actionview.ActionView
import kales.actionview.formFor
import kotlinx.html.*
import kales.actionview.KalesFormMethod.delete
import kotlinx.html.InputType.*

class IndexView(
  bindings: IndexViewModel? = IndexViewModel("Unknown", listOf())
) : ActionView<IndexViewModel>(bindings) {
  override fun FlowContent.render() {
    div("container") {
      div("row") {
        div("col-sm-8 offset-sm-2") {
          h2 { +"Hello, ${bindings?.name}" }
        }
      }
      div("row") {
        div("col-sm-8 offset-sm-2") {
          p { +"Below you can see a list of all posts" }
        }
      }
      div("row") {
        div("col-sm-8 offset-sm-2") {
          h3 { +"Posts" }
        }
      }
      div("row") {
        div("col-sm-8 offset-sm-2") {
          if (bindings?.posts?.any() == true) {
            ul(classes = "list-group") {
              bindings.posts.forEach { p ->
                li(classes = "list-group-item") {
                  a(href = "/posts/${p.id}") { +p.title }
                  formFor(p, method = delete, classes = "float-right") {
                    input(type = submit, classes = "btn btn-outline-danger btn-sm") {
                      value = "Delete"
                    }
                  }
                }
              }
            }
          } else {
            p { +"No posts yet." }
          }
        }
      }
      div("row") {
        div("col-sm-8 offset-sm-2") {
          a(href = "/posts/new", classes = "btn btn-outline-primary") {
            +"Write a new post"
          }
        }
      }
    }
  }
}
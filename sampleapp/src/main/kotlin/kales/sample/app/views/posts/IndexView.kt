package kales.sample.app.views.posts

import kales.actionview.ActionView
import kales.sample.app.views.shared.PostPartialView
import kotlinx.html.*

class IndexView(
    bindings: IndexViewModel? = IndexViewModel("Unknown", listOf())
) : ActionView<IndexViewModel>(bindings) {
  override fun FlowContent.render() {
    div("container") {
      div("row") {
        div("col-sm-12") {
          h2 { +"Hello, ${bindings?.name}" }
        }
      }
      div("row") {
        div("col-sm-12") {
          p { +"Below you can see a list of all posts" }
        }
      }
      div("row") {
        div("col-sm-12") {
          h3 { +"Posts" }
        }
      }
      div("row") {
        div("col-sm-12") {
          if (bindings?.posts?.any() == true) {
            ul {
              bindings.posts.forEach { p ->
                li {
                  a(href = "/posts/${p.id}") {
                    +p.title
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
        div("col-sm-12") {
          a("/posts/new") {
            +"Write a post"
          }
        }
      }
    }
  }
}
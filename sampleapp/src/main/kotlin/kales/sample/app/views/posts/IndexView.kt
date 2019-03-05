package kales.sample.app.views.posts

import kales.actionview.ActionView
import kales.sample.app.views.shared.PostPartialView
import kotlinx.html.*

class IndexView(
    bindings: IndexViewModel? = IndexViewModel("Unknown", listOf())
) : ActionView<IndexViewModel>(bindings) {
  override fun FlowContent.render() {
    h2 {
      +"Hello, ${bindings?.name}"
    }
    p {
      +"Below you can see a list of posts"
    }
    h3 {
      +"Posts"
    }
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
    a("/posts/new") {
      +"Write a post"
    }
  }
}
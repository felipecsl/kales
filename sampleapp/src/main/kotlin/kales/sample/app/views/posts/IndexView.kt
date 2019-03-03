package kales.sample.app.views.posts

import kales.actionview.ActionView
import kotlinx.html.*

class IndexView(
    bindings: IndexViewModel? = IndexViewModel("Unknown", listOf())
) : ActionView<IndexViewModel>(bindings) {
  override fun render(content: FlowContent) {
    content.apply {
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
          bindings.posts.forEach { v ->
            li {
              +v.title
            }
          }
        }
      } else {
        p { +"No posts yet." }
        a("/posts/new") {
          +"Write a post"
        }
      }
    }
  }
}
package kales.sample.app.views.shared

import kales.actionview.ActionPartialView
import kales.sample.app.views.posts.PostViewModel
import kotlinx.html.FlowContent
import kotlinx.html.h3
import kotlinx.html.p

class PostPartialView(
    bindings: PostViewModel? = PostViewModel()
) : ActionPartialView<PostViewModel>(bindings) {
  override fun FlowContent.render() {
    h3 {
      +"${bindings?.post?.title}"
    }
    p {
      +"${bindings?.post?.content}"
    }
  }
}
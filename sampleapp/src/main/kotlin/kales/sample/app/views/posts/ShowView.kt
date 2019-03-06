package kales.sample.app.views.posts

import kales.actionview.ActionView
import kales.sample.app.views.shared.PostPartialView
import kotlinx.html.FlowContent
import kotlinx.html.h2

class ShowView(
    bindings: PostViewModel? = PostViewModel()
) : ActionView<PostViewModel>(bindings) {
  override fun FlowContent.render() {
    h2 { +"Details" }
    renderPartial(PostPartialView(bindings))
  }
}
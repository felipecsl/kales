package kales.sample.app.views.posts

import kales.actionpack.KalesApplicationCall
import kales.actionview.ActionView
import kales.sample.app.views.shared.PostPartialView
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.h2

class WriteCommentView(
  call: KalesApplicationCall,
  bindings: PostViewModel? = PostViewModel()
) : ActionView<PostViewModel>(call, bindings) {
  override fun FlowContent.render() {
    div("container") {
      div("row") {
        div("col-sm-12") {
          h2 { +"Comment added to Post" }
        }
      }
    }
    renderPartial(PostPartialView(bindings))
  }
}
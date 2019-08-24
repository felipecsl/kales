package kales.sample.app.views.posts

import kales.actionview.ActionView
import kales.actionview.KalesFormMethod
import kales.sample.app.views.shared.PostPartialView
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.h2

class ShowView(
  bindings: PostViewModel? = PostViewModel()
) : ActionView<PostViewModel>(bindings) {
  override fun FlowContent.render() {
    div("container") {
      div("row") {
        div("col-sm-8 offset-sm-2") {
          h2 { +"Details" }
        }
      }
    }
    renderPartial(PostPartialView(bindings, KalesFormMethod.put))
  }
}
package kales.actionview

import io.ktor.html.insert
import kotlinx.html.FlowContent

abstract class ActionView<T : ViewModel>(val bindings: T? = null) {
  fun renderContent(content: FlowContent) {
    content.render()
  }

  abstract fun FlowContent.render()

  fun FlowContent.renderPartial(partialView: ActionPartialView<*>) =
      insert(partialView)
}
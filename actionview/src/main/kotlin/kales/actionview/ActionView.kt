package kales.actionview

import kotlinx.html.FlowContent

abstract class ActionView<T : ViewModel>(val bindings: T? = null) {
  abstract fun render(content: FlowContent)
}
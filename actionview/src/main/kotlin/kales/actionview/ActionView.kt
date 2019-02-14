package kales.actionview

import kotlinx.html.HTML

abstract class ActionView<T : ViewModel>(val html: HTML) {
  abstract fun render(bindings: T? = null)
}
package krails.actionview

import kotlinx.html.HTML

abstract class ActionView(val html: HTML) {
  abstract fun render()
}
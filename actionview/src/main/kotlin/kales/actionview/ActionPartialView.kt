package kales.actionview

import io.ktor.html.Placeholder
import kotlinx.html.FlowContent

abstract class ActionPartialView<T : ViewModel>(
    val bindings: T? = null
) : Placeholder<FlowContent>() {
  abstract fun FlowContent.render()

  init {
    invoke {
      render()
    }
  }
}

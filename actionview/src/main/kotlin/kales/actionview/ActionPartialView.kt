package kales.actionview

import io.ktor.html.Placeholder
import kales.actionpack.ViewModel
import kotlinx.html.FlowContent

/**
 * The base class that's used for rendering sub templates into the DOM.
 * The [bindings] object can be used for passing data into the layout rendering step.
 * See also: [ActionView]
 */
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

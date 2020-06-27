package kales.actionview

import io.ktor.html.insert
import kales.actionpack.KalesApplicationCall
import kales.actionpack.ViewModel
import kotlinx.html.FlowContent

/**
 * The base class that's used for rendering templates into the DOM.
 * The [bindings] object can be used for passing data into the layout rendering step.
 * See also: [render]
 */
abstract class ActionView<T : ViewModel>(
  protected val call: KalesApplicationCall,
  protected val bindings: T? = null
) {
  val flash = call.flash

  fun renderContent(content: FlowContent) {
    content.render()
  }

  /** This method is responsible for the actual layout rendering into the page. */
  abstract fun FlowContent.render()

  /** Renders an [ActionPartialView] sub template within the current layout. */
  fun FlowContent.renderPartial(partialView: ActionPartialView<*>) =
      insert(partialView)
}
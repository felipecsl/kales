package kales.actionview

import io.ktor.html.insert
import kales.actionpack.KalesApplicationCall
import kales.actionpack.ViewModel
import kotlinx.html.FlowContent

/** TODO document public API */
abstract class ActionView<T : ViewModel>(
  protected val call: KalesApplicationCall,
  protected val bindings: T? = null
) {
  val flash = call.flash

  fun renderContent(content: FlowContent) {
    content.render()
  }

  abstract fun FlowContent.render()

  fun FlowContent.renderPartial(partialView: ActionPartialView<*>) =
      insert(partialView)
}
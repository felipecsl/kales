package kales.actionview

import io.ktor.html.insert
import kales.actionpack.KalesApplicationCall
import kales.actionpack.ViewModel
import kotlinx.html.FlowContent

// Need `flash` here, but dont have at the time of View instantiation, especially when it is
// explicitly created during the action execution (we have otherwise when its reflectively
// instantiated via inference in KalesApplication.)
// Maybe inject KalesApplicationCall in here too??
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
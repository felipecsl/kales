package kales.actionview

import io.ktor.html.Placeholder
import io.ktor.html.Template
import kales.actionpack.KalesApplicationCall
import kotlinx.html.FlowContent
import kotlinx.html.HTML

abstract class ApplicationLayout(
  protected val call: KalesApplicationCall
) : Template<HTML> {
  val body = Placeholder<FlowContent>()
  val flash = call.flash
}
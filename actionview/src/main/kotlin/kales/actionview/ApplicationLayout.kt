package kales.actionview

import io.ktor.html.Placeholder
import io.ktor.html.Template
import kotlinx.html.FlowContent
import kotlinx.html.HTML

abstract class ApplicationLayout : Template<HTML> {
  val body = Placeholder<FlowContent>()
}
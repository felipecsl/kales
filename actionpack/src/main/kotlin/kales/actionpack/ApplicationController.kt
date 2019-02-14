package kales.actionpack

import kales.actionview.ActionView
import kotlinx.html.HTML

abstract class ApplicationController {
  open fun index(): ActionView<*>? = null

  open fun show(): ActionView<*>? = null

  open fun create(): ActionView<*>? = null

  open fun new(): ActionView<*>? = null

  companion object {
    fun newInstance() {
    }
  }
}
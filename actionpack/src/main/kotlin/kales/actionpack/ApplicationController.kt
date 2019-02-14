package kales.actionpack

import kotlinx.html.HTML

abstract class ApplicationController {
  open fun index(): ((HTML) -> Unit)? = null

  open fun show(): ((HTML) -> Unit)? = null

  open fun create(): ((HTML) -> Unit)? = null

  open fun new(): ((HTML) -> Unit)? = null

  companion object {
    fun newInstance() {
    }
  }
}
package kales.actionview

import kales.actionpack.ActionResult

/**
 * Represents the result of executing a `ApplicationController` action when a [ActionView] view
 * should be rendered. The name of the action executed is represented by [actionName].
 */
data class RenderViewResult(
  val view: ActionView<*>?,
  val actionName: String
) : ActionResult
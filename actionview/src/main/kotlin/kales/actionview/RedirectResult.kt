package kales.actionview

/** Represents the outcome of executing a Kales Controller action */
sealed class ActionResult

/**
 * Represents the result of executing a `ApplicationController` action when a redirect to a new
 * action was requested. The name of the new action is represented by [newActionName].
 */
data class RedirectResult(
  val newActionName: String
) : ActionResult()

data class RenderViewResult(
  val view: ActionView<*>?,
  val actionName: String
) : ActionResult()
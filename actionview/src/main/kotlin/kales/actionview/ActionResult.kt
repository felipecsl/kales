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

/**
 * Represents the result of executing a `ApplicationController` action when a [ActionView] view
 * should be rendered. The name of the action executed is represented by [actionName].
 */
data class RenderViewResult(
  val view: ActionView<*>?,
  val actionName: String
) : ActionResult()
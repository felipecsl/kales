package kales.actionpack

/** Represents the outcome of executing a Kales Controller action */
interface ActionResult

/**
 * Returns a [RedirectResult] for redirecting to the named action.
 */
fun redirect(newActionName: String): RedirectResult = RedirectResult(newActionName)

/**
 * Alias for [redirect] matching Rails redirect_to naming.
 */
fun redirectTo(newActionName: String): RedirectResult = redirect(newActionName)

/**
 * Represents the result of executing a `ApplicationController` action when a redirect to a new
 * action was requested. The name of the new action is represented by [newActionName].
 */
data class RedirectResult(
  val newActionName: String
) : ActionResult
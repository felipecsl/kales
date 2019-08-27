package kales.actionpack

/** Represents the outcome of executing a Kales Controller action */
interface ActionResult

/**
 * Represents the result of executing a `ApplicationController` action when a redirect to a new
 * action was requested. The name of the new action is represented by [newActionName].
 */
data class RedirectResult(
  val newActionName: String
) : ActionResult
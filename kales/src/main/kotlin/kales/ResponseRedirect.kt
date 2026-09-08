package kales

import io.ktor.application.*
import io.ktor.http.*
import kales.actionpack.RedirectResult

/**
 * Sends a redirect response to the action named by [result.newActionName].
 */
fun ApplicationCall.redirectTo(result: RedirectResult, permanent: Boolean = false) {
  respondRedirect("/${result.newActionName}", permanent)
}

/**
 * Alias for [redirectTo] matching the Ktor respondRedirect naming.
 */
fun ApplicationCall.respondRedirect(result: RedirectResult, permanent: Boolean = false) {
  redirectTo(result, permanent)
}

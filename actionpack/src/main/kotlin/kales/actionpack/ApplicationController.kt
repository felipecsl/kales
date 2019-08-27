package kales.actionpack

import io.ktor.http.Parameters
import io.ktor.http.plus
import kotlin.reflect.KFunction

abstract class ApplicationController(val call: KalesApplicationCall) {
  var bindings: ViewModel? = null

  val flash = call.flash

  suspend fun receiveParameters(): Parameters {
    val parameters = call.receiveRoutingParameters()
    // URL parameters are fetched with call.parameters instead, it's a bit weird, but we'll
    // concatenate both Paramters together so we conveniently return everything together.
    // TODO: What would happen if we have a form parameter and a URL parameter with the same name
    //  eg.: "id", I assume in this case only one of the two would be returned, we should instead
    //  return all parameter matches for a specific key. Sounds like this should be a map from
    //  String to a List<String>. Apparently `Parameters` already supports this with `getAll()`,
    //  we should just write a test to validate that scenario.
    return parameters + call.parameters
  }

  /**
   * Redirects to the action specified by the `action` function parameter. The redirection is purely
   * server-side, there is no round-trip to the client browser and it's seen strictly as a single
   * request from the client's perspective.
   */
  fun redirectTo(action: KFunction<*>): RedirectResult {
    return RedirectResult(action.name)
  }
}
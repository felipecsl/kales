package kales.actionpack

import io.ktor.application.ApplicationCall
import io.ktor.request.ApplicationRequest
import io.ktor.http.Parameters
import io.ktor.http.plus
import io.ktor.request.receiveParameters
import io.ktor.routing.RoutingApplicationCall
import kales.actionview.RedirectResult
import kales.actionview.ViewModel
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

abstract class ApplicationController(val call: ApplicationCall) {
  var bindings: ViewModel? = null

  /**
   * RoutingApplicationCall wraps another (inner) call (our Kales call), so we need to unwrap it
   * and cast it to [KalesApplicationCall] in order to benefit from the memoized `receiveParameters`
   * implementation.
   *
   * We cannot simply allow users to call [RoutingApplicationCall.receiveParameters] because that
   * is defined as an extension method, which we cannot override like regular class methods.
   *
   * Doing that will thus cause [ApplicationRequest.receiveChannel] to try to read from
   * the stream again, failing to read anyting (because we already did so from
   * [DynamicParameterRouteSelector]).
   */
  suspend fun receiveParameters(): Parameters {
    val parameters = if (call is RoutingApplicationCall) {
      (call.javaClass.kotlin.declaredMemberProperties
        .find { it.name == "call" }!!
        .also { it.isAccessible = true }
        .get(call) as KalesApplicationCall)
        .receiveParameters()
    } else {
      call.receiveParameters()
    }
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
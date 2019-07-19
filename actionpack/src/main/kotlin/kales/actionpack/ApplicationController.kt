package kales.actionpack

import io.ktor.application.ApplicationCall
import io.ktor.request.ApplicationRequest
import io.ktor.http.Parameters
import io.ktor.http.plus
import io.ktor.request.receiveParameters
import io.ktor.routing.RoutingApplicationCall
import kales.actionview.ViewModel
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
    return parameters + call.parameters
  }
}
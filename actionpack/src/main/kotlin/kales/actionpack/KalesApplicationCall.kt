package kales.actionpack

import io.ktor.application.ApplicationCall
import io.ktor.http.Parameters
import io.ktor.request.ApplicationRequest
import io.ktor.request.receive
import io.ktor.routing.RoutingApplicationCall
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/** TODO document */
class KalesApplicationCall(private val delegate: ApplicationCall) : ApplicationCall {
  private var requestBody: Any? = null

  override val application
    get() = delegate.application

  override val attributes
    get() = delegate.attributes

  override val parameters
    get() = delegate.parameters

  override val request
    get() = delegate.request

  override val response
    get() = delegate.response

  /** The flash provides a way to pass temporary primitive-types (String, List, Map) between actions. */
  val flash: MutableMap<String, Any> = mutableMapOf()

  private val isRoutingApplicationCall = delegate is RoutingApplicationCall

  /**
   * [RoutingApplicationCall] wraps another (inner) call (our Kales call), so we need to unwrap it
   * and cast it to [KalesApplicationCall] in order to benefit from the memoized `receiveParameters`
   * implementation.
   *
   * We cannot simply allow users to call [RoutingApplicationCall.recereceiveParameters] because that
   * is defined as an extension method, which we cannot override like regular class methods.
   *
   * Doing that will thus cause [ApplicationRequest.receiveChannel] to try to read from
   * the stream again, failing to read anyting (because we already did so from
   * [DynamicParameterRouteSelector]).
   */
  suspend fun receiveRoutingParameters(): Parameters {
    return if (isRoutingApplicationCall) {
      (delegate.javaClass.kotlin.declaredMemberProperties
        .find { it.name == "call" }!!
        .also { it.isAccessible = true }
        .get(delegate) as KalesApplicationCall)
        .receiveParameters()
    } else {
      receiveParameters()
    }
  }

  suspend inline fun receiveParameters(): Parameters = receive(Parameters::class)

  suspend fun <T : Any> receive(type: KClass<T>): T {
    if (requestBody == null) {
      requestBody = delegate.receive(type)
    }
    @Suppress("UNCHECKED_CAST")
    return requestBody as T
  }
}
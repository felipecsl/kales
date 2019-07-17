package kales.actionpack

import io.ktor.application.ApplicationCall
import io.ktor.http.Parameters
import io.ktor.request.receive
import kotlin.reflect.KClass

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

  suspend inline fun receiveParameters(): Parameters = receive(Parameters::class)

  suspend fun <T : Any> receive(type: KClass<T>): T {
    if (requestBody == null) {
      requestBody = delegate.receive(type)
    }
    @Suppress("UNCHECKED_CAST")
    return requestBody as T
  }
}
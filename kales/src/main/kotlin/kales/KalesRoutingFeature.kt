package kales

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.routing.Routing
import io.ktor.util.AttributeKey
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

internal object KalesRoutingFeature : ApplicationFeature<Application, Routing, Routing> {
  override val key: AttributeKey<Routing> = AttributeKey("KalesRouting")

  override fun install(pipeline: Application, configure: Routing.() -> Unit): Routing {
    val routing = Routing(pipeline).apply(configure)
    pipeline.intercept(ApplicationCallPipeline.Call) {
      routing.callSuspendMethod("interceptor", KalesPipelineContext(this))
    }
    return routing
  }
}

/** Reflectively calls the provided suspend method with args */
suspend fun Any.callSuspendMethod(methodName: String, vararg args: Any?) {
  val klass = javaClass.kotlin
  val method = klass.declaredFunctions
    .find { it.name == methodName }
    ?.also { it.isAccessible = true }
    ?: throw IllegalStateException("Failed to find `$klass#$methodName` method")
  method.callSuspend(this, *args)
}
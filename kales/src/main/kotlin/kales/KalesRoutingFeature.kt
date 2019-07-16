package kales

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.routing.Routing
import io.ktor.util.AttributeKey

internal object KalesRoutingFeature : ApplicationFeature<Application, Routing, Routing> {
  override val key: AttributeKey<Routing> = AttributeKey("KalesRouting")

  override fun install(pipeline: Application, configure: Routing.() -> Unit): Routing {
    val kalesRouting = KalesRouting(Routing(pipeline).apply(configure))
    pipeline.intercept(ApplicationCallPipeline.Call) { kalesRouting.interceptor(this) }
    return kalesRouting.routing
  }
}
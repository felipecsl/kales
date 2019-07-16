package kales

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.RoutingResolveContext
import io.ktor.routing.RoutingResolveResult
import io.ktor.routing.RoutingResolveTrace
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * This class wraps [Routing] and decorates [ApplicationCall] with [KalesApplicationCall] allowing
 * us to receive the request data from [ApplicationCall] and memoize its contents so it can be
 * called multiple times. This is needed especially because [DynamicParameterRouteSelector] needs
 * to be able to call that multiple times for the lifetime of a request.
 */
internal class KalesRouting(internal val routing: Routing) {
  internal suspend fun interceptor(context: PipelineContext<Unit, ApplicationCall>) {
    val routingClass = routing.javaClass.kotlin
    val tracersProp = routingClass.declaredMemberProperties.find { it.name == "tracers" }
      ?: throw IllegalStateException("Failed to find `Routing.tracers` property")
    tracersProp.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    val tracers = tracersProp.get(routing) as List<(RoutingResolveTrace) -> Unit>
    val resolveContext = RoutingResolveContext(routing, KalesApplicationCall(context.call), tracers)
    val resolveResult = resolveContext.resolve()
    if (resolveResult is RoutingResolveResult.Success) {
      val executeResultMethod = routingClass.declaredFunctions.find { it.name == "executeResult" }
        ?: throw IllegalStateException("Failed to find `Routing#executeResult` method")
      executeResultMethod.isAccessible = true
      executeResultMethod.callSuspend(routing, context, resolveResult.route,
        resolveResult.parameters)
    }
  }
}
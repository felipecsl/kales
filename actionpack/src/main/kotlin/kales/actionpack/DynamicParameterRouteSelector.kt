package kales.actionpack

import io.ktor.routing.RouteSelector
import io.ktor.routing.RouteSelectorEvaluation
import io.ktor.routing.RoutingResolveContext
import kotlinx.coroutines.runBlocking

data class DynamicParameterRouteSelector(
  val name: String,
  val value: String
) : RouteSelector(RouteSelectorEvaluation.qualityConstant) {
  override fun evaluate(
    context: RoutingResolveContext,
    segmentIndex: Int
  ): RouteSelectorEvaluation {
    return runBlocking {
      val kalesCall = context.call as KalesApplicationCall
      val parameters = kalesCall.receiveParameters()
      if (parameters.contains(name, value)) {
        RouteSelectorEvaluation.Constant
      } else {
        RouteSelectorEvaluation.Failed
      }
    }
  }

  override fun toString(): String = "[$name = $value]"
}
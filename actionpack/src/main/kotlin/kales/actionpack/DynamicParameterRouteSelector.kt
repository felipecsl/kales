package kales.actionpack

import io.ktor.features.ContentTransformationException
import io.ktor.http.Parameters
import io.ktor.routing.RouteSelector
import io.ktor.routing.RouteSelectorEvaluation
import io.ktor.routing.RoutingResolveContext
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking

data class DynamicParameterRouteSelector(
  val name: String,
  val value: String
) : RouteSelector(RouteSelectorEvaluation.qualityConstant) {
  @KtorExperimentalAPI
  override fun evaluate(
    context: RoutingResolveContext,
    segmentIndex: Int
  ): RouteSelectorEvaluation {
    return runBlocking {
      val kalesCall = context.call as KalesApplicationCall
      val parameters = try {
        kalesCall.receiveParameters()
      } catch (e: ContentTransformationException) {
        // Cannot transform this request's content to class io.ktor.http.Parameters
        Parameters.Empty
      }
      if (parameters.contains(name, value)) {
        RouteSelectorEvaluation.Constant
      } else {
        RouteSelectorEvaluation.Failed
      }
    }
  }

  override fun toString(): String = "[$name = $value]"
}
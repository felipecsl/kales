package kales

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.util.pipeline.PipelineContext
import kales.actionpack.KalesApplicationCall

/**
 * This custom [PipelineContext] allows us to sneak our [KalesApplicationCall] implementation into
 * [Routing], since whenever it calls `context.call` it ends up ultimately calling the [context]
 * property in this class, which has our memoizable `receiveParamters` implementation.
 */
internal class KalesPipelineContext(
  private val delegate: PipelineContext<Unit, ApplicationCall>
) : PipelineContext<Unit, ApplicationCall> by delegate {
  override val context: ApplicationCall by lazy { KalesApplicationCall(delegate.call) }
}
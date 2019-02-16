package kales

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtmlTemplate
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import kales.actionpack.ApplicationController
import kales.actionview.ActionView
import kales.actionview.ApplicationLayout
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

class KalesApplication<T : ApplicationLayout>(
    private val application: Application,
    val layout: KClass<T>
) {
  lateinit var routing: Routing

  fun initRoutes(routes: KalesApplication<T>.() -> Unit) {
    application.install(DefaultHeaders)
    application.install(CallLogging)
    application.install(Routing) {
      routing = this
      routes()
    }
  }

  inline fun <reified T : ApplicationController> get(
      path: String,
      actionName: String
  ): Route = routing.get(path) {
    val view = reflectView<T>(actionName, call)
    call.respondHtmlTemplate(layout.createInstance()) {
      body {
        view.render(this)
      }
    }
  }

  inline fun <reified T : ApplicationController> reflectView(
      actionName: String,
      call: ApplicationCall
  ): ActionView<*> {
    val controllerClassName = T::class.simpleName?.replace("Controller", "")?.toLowerCase()
        ?: throw RuntimeException("Cannot determine the class name for Controller")
    @Suppress("UNCHECKED_CAST")
    val controllerCtor = T::class.primaryConstructor ?: throw RuntimeException(
        "Primary constructor not found for Controller class $controllerClassName")
    val controller = controllerCtor.call(call)
    val actionMethod = controller.javaClass.getMethod(actionName)
    val view = actionMethod.invoke(controller) as? ActionView<*>
    return if (view != null) {
      view
    } else {
      val viewClassName = "${actionName.capitalize()}View"
      val viewFullyQualifiedClassName = "kales.sample.app.views.$controllerClassName.$viewClassName"
      @Suppress("UNCHECKED_CAST")
      val viewClass = Class.forName(viewFullyQualifiedClassName) as Class<ActionView<*>>
      viewClass.kotlin.primaryConstructor?.call(controller.bindings)
          ?: throw RuntimeException("Unable to find primary constructor for $viewClass")
    }
  }
}
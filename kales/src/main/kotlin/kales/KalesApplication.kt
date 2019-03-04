package kales

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import kales.actionpack.ApplicationController
import kales.actionview.ActionView
import kales.actionview.ApplicationLayout
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredFunctions
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
      static("assets") {
        staticRootFolder = File("assets")
        files("javascripts")
        files("stylesheets")
        files("images")
      }
    }
  }

  inline fun <reified T : ApplicationController> get(
      path: String,
      actionName: String
  ): Route = routing.get(path) {
    val view = callControllerAction<T>(actionName, call)
    call.respondHtmlTemplate(layout.createInstance()) {
      body {
        view.render(this)
      }
    }
  }

  inline fun <reified T : ApplicationController> post(
      path: String,
      actionName: String
  ): Route = routing.post(path) {
    val view = callControllerAction<T>(actionName, call)
    call.respondHtmlTemplate(layout.createInstance()) {
      body {
        view.render(this)
      }
    }
  }

  suspend inline fun <reified T : ApplicationController> callControllerAction(
      actionName: String,
      call: ApplicationCall
  ): ActionView<*> {
    val controllerClassName = T::class.simpleName?.replace("Controller", "")?.toLowerCase()
        ?: throw RuntimeException("Cannot determine the class name for Controller")
    @Suppress("UNCHECKED_CAST")
    val controllerCtor = T::class.primaryConstructor ?: throw RuntimeException(
        "Primary constructor not found for Controller class $controllerClassName")
    val controller = controllerCtor.call(call)
    val actionMethod = controller::class.declaredFunctions.firstOrNull { it.name == actionName }
        ?: throw RuntimeException("Cannot find Controller action $actionName")
    val view = if (actionMethod.isSuspend) {
      actionMethod.callSuspend(controller) as? ActionView<*>
    } else {
      actionMethod.call(controller) as? ActionView<*>
    }
    return if (view != null) {
      // If the action returned a View object, we'll use that
      view
    } else {
      // Otherwise, search for the inferred view class
      val viewClass = findViewClass<T>(actionName, controllerClassName)
      viewClass.kotlin.primaryConstructor?.call(controller.bindings)
          ?: throw RuntimeException("Unable to find primary constructor for $viewClass")
    }
  }

  /**
   * Seearches for a view class matching this controller action, for example:
   * "FooController" controller, "index" action, the searched class is
   * "com.example.app.views.foo.IndexView"
   */
  inline fun <reified T : ApplicationController> findViewClass(
      actionName: String,
      controllerClassName: String
  ): Class<ActionView<*>> {
    val applicationPackage = extractAppPackageNameFromControllerClass<T>()
    val viewClassName = "${actionName.capitalize()}View"
    val viewFullyQualifiedName = "$applicationPackage.views.$controllerClassName.$viewClassName"
    return try {
      @Suppress("UNCHECKED_CAST")
      Class.forName(viewFullyQualifiedName) as Class<ActionView<*>>
    } catch (e: ClassNotFoundException) {
      throw RuntimeException("Unable to find view class $viewFullyQualifiedName")
    }
  }

  /**
   * Takes a controller class name, eg: "com.example.app.controllers.FooController".
   * Returns "com.example.app"
   * */
  inline fun <reified T : ApplicationController> extractAppPackageNameFromControllerClass() =
      (T::class.qualifiedName
          ?.split(".")
          ?.takeWhile { it != "controllers" }
          ?.joinToString(".")
          ?: throw RuntimeException("Cannot determine the full class name for Controller"))
}

fun <T : ApplicationLayout> Application.kalesApp(
    layout: KClass<T>,
    routes: KalesApplication<T>.() -> Unit
) {
  KalesApplication(this, layout).initRoutes(routes)
}
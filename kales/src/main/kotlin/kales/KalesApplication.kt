package kales

import io.ktor.application.*
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
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.primaryConstructor

class KalesApplication<T : ApplicationLayout>(
  private val application: Application,
  val layout: KClass<T>,
  val routes: MutableSet<RequestRoute<*>> = mutableSetOf()
) {
  lateinit var routing: Routing

  fun initRoutes(routes: KalesApplication<T>.() -> Unit) {
    application.install(DefaultHeaders)
    application.install(CallLogging)
    application.install(Routing) {
      trace { application.log.trace(it.buildText()) }
      routing = this
      routes.invoke(this@KalesApplication)
      static("assets") {
        staticRootFolder = File("assets")
        files("javascripts")
        files("stylesheets")
        files("images")
      }
      installCustomRoutes()
    }
  }

  /**
   * Install a catch-all route that captures POST requests to any path and looks for a `_method`
   * form parameter. If one is found, we then look for a DELETE/PATCH/PUT handler that matches that
   * and rout to that handler instead. This is to work around a limitation where browsers don't
   * support those verbs (only GET and POST) and it's the same workaround that Rails uses.
   */
  private fun installCustomRoutes() {
    routing.post("/{path...}") {
      val path = context.parameters["path"]
      val kalesMethod = context.request.call.parameters["_method"]
      if (kalesMethod != null) {
        logger.info("Looking up handler for request ${kalesMethod.toUpperCase()} $path")
        val handler = routes.firstOrNull { it.method == kalesMethod && it.path == path }
        if (handler != null) {
          val view = callControllerAction(handler.controllerClass, handler.action, call)
          call.respondHtmlTemplate(layout.createInstance()) {
            body {
              view.renderContent(this)
            }
          }
        } else {
          logger.info("No route handler found for ${kalesMethod.toUpperCase()} $path")
        }
      }
    }
  }

  inline fun <reified T : ApplicationController> get(
    path: String,
    actionName: String
  ): Route = routing.get(path) {
    val view = callControllerAction(T::class, actionName, call)
    call.respondHtmlTemplate(layout.createInstance()) {
      body {
        view.renderContent(this)
      }
    }
  }

  inline fun <reified T : ApplicationController> post(
    path: String,
    actionName: String
  ): Route = routing.post(path) {
    val view = callControllerAction(T::class, actionName, call)
    call.respondHtmlTemplate(layout.createInstance()) {
      body {
        view.renderContent(this)
      }
    }
  }

  inline fun <reified T : ApplicationController> put(
    path: String,
    actionName: String
  ) = routes.add(RequestRoute(T::class, "put", path, actionName))

  inline fun <reified T : ApplicationController> delete(
    path: String,
    actionName: String
  ) = routes.add(RequestRoute(T::class, "delete", path, actionName))

  inline fun <reified T : ApplicationController> patch(
    path: String,
    actionName: String
  ) = routes.add(RequestRoute(T::class, "patch", path, actionName))

  suspend inline fun <T : ApplicationController> callControllerAction(
    controllerClass: KClass<T>,
    actionName: String,
    call: ApplicationCall
  ): ActionView<*> {
    val controllerClassName = controllerClass.simpleName?.replace("Controller", "")?.toLowerCase()
      ?: throw RuntimeException("Cannot determine the class name for Controller")
    @Suppress("UNCHECKED_CAST")
    val controllerCtor = controllerClass.primaryConstructor ?: throw RuntimeException(
      "Primary constructor not found for Controller class $controllerClassName")
    val controller = try {
      controllerCtor.call(call)
    } catch (e: RuntimeException) {
      throw RuntimeException("Failed to instantiate controller $controllerClass", e)
    }
    val actionMethod = controller::class.declaredFunctions.firstOrNull { it.name == actionName }
      ?: throw RuntimeException("Cannot find Controller action $actionName")
    logger.info("Calling $controllerClassName#$actionName")
    val view = if (actionMethod.isSuspend) {
      actionMethod.callSuspend(controller)
    } else {
      actionMethod.call(controller)
    } as? ActionView<*>
    return if (view != null) {
      // If the action returned a View object, we'll use that
      view
    } else {
      // Otherwise, search for the inferred view class
      val viewClass = findViewClass(controllerClass, actionName, controllerClassName)
      try {
        viewClass.kotlin.primaryConstructor?.call(controller.bindings)
          ?: throw RuntimeException("Unable to find primary constructor for $viewClass")
      } catch (e: RuntimeException) {
        throw RuntimeException("Failed to instantiate view $viewClass", e)
      }
    }
  }

  /**
   * Seearches for a view class matching this controller action, for example:
   * "FooController" controller, "index" action, the searched class is
   * "com.example.app.views.foo.IndexView"
   */
  inline fun <T : ApplicationController> findViewClass(
    controllerClass: KClass<T>,
    actionName: String,
    controllerClassName: String
  ): Class<ActionView<*>> {
    val applicationPackage = extractAppPackageNameFromControllerClass(controllerClass)
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
  inline fun <T : ApplicationController> extractAppPackageNameFromControllerClass(
    controllerClass: KClass<T>
  ) =
    (controllerClass.qualifiedName
      ?.split(".")
      ?.takeWhile { it != "controllers" }
      ?.joinToString(".")
      ?: throw RuntimeException("Cannot determine the full class name for Controller"))

  companion object {
    val logger = Logger.getLogger(KalesApplication::class.simpleName)
  }
}

fun <T : ApplicationLayout> Application.kalesApp(
  layout: KClass<T>,
  routes: KalesApplication<T>.() -> Unit
) {
  KalesApplication(this, layout).initRoutes(routes)
}
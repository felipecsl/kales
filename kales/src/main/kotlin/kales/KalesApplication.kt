package kales

import io.ktor.application.*
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.http.content.staticRootFolder
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext
import kales.actionpack.ApplicationController
import kales.actionpack.DynamicParameterRouteSelector
import kales.actionview.*
import java.io.File
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.primaryConstructor

class KalesApplication<T : ApplicationLayout>(
  private val application: Application,
  private val layout: KClass<T>
) {
  lateinit var routing: Routing

  fun initRoutes(routes: KalesApplication<T>.() -> Unit) {
    application.install(DefaultHeaders)
    application.install(CallLogging)
    application.install(KalesRoutingFeature) {
      trace { application.log.trace(it.buildText()) }
      routing = this
      routes.invoke(this@KalesApplication)
      static("assets") {
        staticRootFolder = File("assets")
        files("javascripts")
        files("stylesheets")
        files("images")
      }
    }
  }

  inline fun <reified T : ApplicationController> get(path: String, actionName: String) =
    routing.get(path, defaultRequestHandler(T::class, actionName))

  inline fun <reified T : ApplicationController> post(path: String, actionName: String) =
    routing.post(path, defaultRequestHandler(T::class, actionName))

  inline fun <reified T : ApplicationController> put(path: String, actionName: String) =
    createCustomRequestMethodViaFormParamHandler(T::class, path, actionName, "put")

  inline fun <reified T : ApplicationController> delete(path: String, actionName: String) =
    createCustomRequestMethodViaFormParamHandler(T::class, path, actionName, "delete")

  inline fun <reified T : ApplicationController> patch(path: String, actionName: String) =
    createCustomRequestMethodViaFormParamHandler(T::class, path, actionName, "patch")

  fun <T : ApplicationController> defaultRequestHandler(
    controllerClass: KClass<T>,
    actionName: String
  ):
    suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit {
    return {
      val view = callControllerAction(controllerClass, actionName, call)
      if (view != null) {
        call.respondHtmlTemplate(layout.createInstance()) {
          body {
            // TODO: #70 Respond with 404 when a view was not found
            view.renderContent(this)
          }
        }
      } else {
        call.respond(HttpStatusCode.NotFound, "Not Found")
      }
    }
  }

  /**
   * Install a catch-all route that captures POST requests to any path and looks for a `_method`
   * form parameter. If one is found, we then look for a DELETE/PATCH/PUT handler that matches that
   * and rout to that handler instead. This is to work around a limitation where browsers don't
   * support those verbs (only GET and POST) and it's the same workaround that Rails uses.
   */
  fun <T : ApplicationController> createCustomRequestMethodViaFormParamHandler(
    controllerClass: KClass<T>,
    path: String,
    actionName: String,
    method: String
  ) {
    routing.createRouteFromPath(path)
      .createChild(DynamicParameterRouteSelector("_method", method))
      .createChild(HttpMethodRouteSelector(HttpMethod.Post))
      .apply {
        handle(defaultRequestHandler(controllerClass, actionName))
      }
  }

  private suspend fun <T : ApplicationController> callControllerAction(
    controllerClass: KClass<T>,
    actionName: String,
    call: ApplicationCall
  ): ActionView<*>? {
    @Suppress("UNCHECKED_CAST")
    val controllerCtor = controllerClass.primaryConstructor()
    val controller = try {
      controllerCtor.call(call)
    } catch (e: RuntimeException) {
      throw RuntimeException("Failed to instantiate controller $controllerClass", e)
    }
    val renderViewResult = recursivelyCallAction(controller, actionName) as RenderViewResult?
    // If the action returned a non-null View object, we'll use that, otherwise, try to infer the
    // corresponding view class and reflectively instantiate it
    return renderViewResult?.view
  }

  private suspend fun <T : ApplicationController> recursivelyCallAction(
    controller: T,
    actionName: String
  ): ActionResult? {
    val controllerClass = controller::class
    val modelName = modelNameFromControllerClass(controllerClass)
    val actionMethod = controller::class.declaredFunctions.firstOrNull { it.name == actionName }
      ?: throw RuntimeException("Cannot find Controller action $actionName")
    logger.info("Calling route $modelName#$actionName")
    val result = if (actionMethod.isSuspend) {
      actionMethod.callSuspend(controller)
    } else {
      actionMethod.call(controller)
    }
    return when (result) {
      is RenderViewResult -> result
      is RedirectResult -> recursivelyCallAction(controller, result.newActionName)
      is ActionView<*> -> RenderViewResult(result, actionName)
      else -> RenderViewResult(inferView(controller, actionName), actionName)
    }
  }

  private fun <T : ApplicationController> inferView(
    controller: T,
    actionName: String
  ): ActionView<out ViewModel>? {
    val viewClass = findViewClass(controller::class, actionName)
    return if (viewClass != null) {
      try {
        val viewConstructor = viewClass.primaryConstructor()
        println("Calling View ctor: '$viewConstructor' with bindings: '${controller.bindings}'")
        viewConstructor.call(controller.bindings)
      } catch (e: RuntimeException) {
        throw RuntimeException("Failed to instantiate view $viewClass", e)
      }
    } else {
      // View not found
      null
    }
  }

  /** Eg.: PostsController -> "posts" */
  private fun <T : ApplicationController> modelNameFromControllerClass(controllerClass: KClass<T>) =
    controllerClass.simpleName?.replace("Controller", "")?.toLowerCase()
      ?: throw RuntimeException("Cannot determine the class name for Controller")

  private fun <T : Any> KClass<T>.primaryConstructor() =
    primaryConstructor ?: throw RuntimeException("Primary constructor not found for $this")

  /**
   * Seearches for a view class matching this controller action, for example:
   * "FooController" controller, "index" action, the searched class is
   * "com.example.app.views.foo.IndexView"
   */
  @Suppress("NOTHING_TO_INLINE")
  private fun <T : ApplicationController> findViewClass(
    controllerClass: KClass<T>,
    actionName: String
  ): KClass<ActionView<*>>? {
    val modelName = modelNameFromControllerClass(controllerClass)
    val applicationPackage = extractAppPackageNameFromControllerClass(controllerClass)
    val viewClassName = "${actionName.capitalize()}View"
    val viewFullyQualifiedName = "$applicationPackage.views.$modelName.$viewClassName"
    return loadViewClass(viewFullyQualifiedName, controllerClass.java.classLoader)
  }

  private fun loadViewClass(viewFullyQualifiedName: String, classLoader: ClassLoader) =
    try {
      @Suppress("UNCHECKED_CAST")
      val clazz = Class.forName(viewFullyQualifiedName, true, classLoader) as Class<ActionView<*>>
      clazz.kotlin
    } catch (e: ClassNotFoundException) {
      logger.warning("Unable to find view class $viewFullyQualifiedName")
      null
    }

  /**
   * Takes a controller class name, eg: "com.example.app.controllers.FooController".
   * Returns "com.example.app"
   */
  @Suppress("NOTHING_TO_INLINE")
  private fun <T : ApplicationController> extractAppPackageNameFromControllerClass(
    controllerClass: KClass<T>
  ) =
    (controllerClass.qualifiedName
      ?.split(".")
      ?.takeWhile { it != "controllers" }
      ?.joinToString(".")
      ?: throw RuntimeException("Cannot determine the full class name for Controller"))

  companion object {
    val logger: Logger = Logger.getLogger(KalesApplication::class.simpleName)
  }
}

/** TODO document public API */
fun <T : ApplicationLayout> Application.kalesApp(
  layout: KClass<T>,
  routes: KalesApplication<T>.() -> Unit
) {
  KalesApplication(this, layout).initRoutes(routes)
}
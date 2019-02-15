package kales

import io.ktor.application.Application
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
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.yaml.snakeyaml.Yaml
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class KalesApplication<T : ApplicationLayout>(
    private val application: Application,
    val layout: KClass<T>
) {
  val jdbi: Jdbi = Jdbi.create(dbConnectionString()).installPlugins()

  init {
    INSTANCE = this
  }

  @Suppress("UNCHECKED_CAST")
  private fun dbConnectionString(): String {
    val yaml = Yaml()
    val stream = KalesApplication::class.java.classLoader.getResourceAsStream("database.yml")
    val data = yaml.load<Map<String, Any>>(stream)
    val devData = data["development"] as Map<String, String>
    val host = devData["host"]
    val database = devData["database"]
    val username = devData["username"]
    val password = devData["password"]
    return "jdbc:postgresql://$host/$database?user=$username&password=$password"
  }

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
      crossinline action: (T) -> ActionView<*>?
  ): Route = routing.get(path) {
    @Suppress("UNCHECKED_CAST")
    val controllerCtor = T::class.java.constructors.first() as java.lang.reflect.Constructor<T>
    val controller = controllerCtor.newInstance()
    val view = action(controller)
    call.respondHtmlTemplate(layout.createInstance()) {
      body {
        view?.render(this)
      }
    }
  }

  companion object {
    lateinit var INSTANCE: KalesApplication<*>
  }
}
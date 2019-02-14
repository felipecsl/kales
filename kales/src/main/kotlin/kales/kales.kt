package kales

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import kales.actionpack.ApplicationController
import kotlinx.html.HTML
import java.lang.reflect.Constructor

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ApplicationController> Route.get(
    path: String,
    crossinline action: (T) -> ((HTML) -> Unit)
) = get(path) {
  val controllerCtor = T::class.java.constructors.first() as Constructor<T>
  val controller = controllerCtor.newInstance()
  call.respondHtml {
    action(controller).invoke(this)
  }
}
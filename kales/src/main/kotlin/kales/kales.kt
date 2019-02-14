package kales

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.Route
import io.ktor.routing.get
import kales.actionpack.ApplicationController
import kales.actionview.ActionView
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ApplicationController, V : ActionView> Route.get(
    path: String,
    crossinline action: (T) -> KClass<V>?
) = get(path) {
  val controllerCtor = T::class.java.constructors.first() as Constructor<T>
  val controller = controllerCtor.newInstance()
  val viewKlass = action(controller)
  if (viewKlass != null) {
    call.respondHtml {
      val viewCtor = viewKlass.java.constructors.first() as Constructor<V>
      viewCtor.newInstance(this).render()
    }
  }
}
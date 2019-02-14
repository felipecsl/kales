package kales

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.html.respondHtmlTemplate
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import kales.actionpack.ApplicationController
import kales.actionview.ApplicationLayout
import kotlinx.html.HTML
import java.lang.reflect.Constructor
import kotlin.reflect.KClass

fun <T : ApplicationLayout> Application.kalesApplication(
    layout: KClass<T>,
    routes: KalesApplication<T>.() -> Unit
) {
  KalesApplication(this, layout).initRoutes(routes)
}
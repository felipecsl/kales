package kales.sample

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.routing.Routing
import kales.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kales.sample.controllers.ExampleController
import kales.sample.views.example.ExampleIndexView

fun Application.module() {
  install(DefaultHeaders)
  install(CallLogging)
  install(Routing) {
    get<ExampleController, ExampleIndexView>("/", ExampleController::index)
  }
}

fun main(args: Array<String>) {
  embeddedServer(
      Netty, 8080,
      watchPaths = listOf("kales"),
      module = Application::module
  ).start()
}
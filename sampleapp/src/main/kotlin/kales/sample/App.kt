package kales.sample

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kales.kalesApplication
import kales.sample.app.controllers.ExampleController
import kales.sample.app.views.layouts.ExampleApplicationLayout

fun Application.module() {
  kalesApplication(ExampleApplicationLayout::class) {
    get("/", ExampleController::index)
    get("/foo", ExampleController::foo)
  }
}

fun main() {
  embeddedServer(
      Netty, 8080,
      watchPaths = listOf("sampleapp"),
      module = Application::module
  ).start()
}
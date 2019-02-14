package kales.sample

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kales.kalesApplication
import kales.sample.app.controllers.ExampleController
import kales.sample.app.views.layouts.SampleApplicationLayout

fun Application.module() {
  kalesApplication(SampleApplicationLayout::class) {
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
package kales.sample

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kales.kalesApp
import kales.sample.app.controllers.PostsController
import kales.sample.app.views.layouts.ExampleApplicationLayout

fun Application.module() {
  kalesApp(ExampleApplicationLayout::class) {
    get<PostsController>("/", "index")
    get<PostsController>("/post/{id}", "show")
  }
}

fun main() {
  embeddedServer(
      Netty, 8080,
      watchPaths = listOf("sampleapp"),
      module = Application::module
  ).start()
}
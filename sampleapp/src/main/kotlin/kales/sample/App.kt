package kales.sample

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kales.KalesApplication
import kales.actionview.ApplicationLayout
import kales.kalesApp
import kales.sample.app.controllers.PostsController
import kales.sample.app.views.layouts.ExampleApplicationLayout

fun Application.module() {
  kalesApp(ExampleApplicationLayout::class, routes())
}

fun main() {
  embeddedServer(
      Netty, 8080,
      watchPaths = listOf("sampleapp"),
      module = Application::module
  ).start()
}
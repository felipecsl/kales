package kales.sample

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kales.kalesApp
import kales.sample.app.views.layouts.AppLayout

fun Application.module() {
  kalesApp(AppLayout::class, routes())
}

fun main() {
  embeddedServer(
      Netty, 8080,
      watchPaths = listOf("sampleapp"),
      module = Application::module
  ).start()
}
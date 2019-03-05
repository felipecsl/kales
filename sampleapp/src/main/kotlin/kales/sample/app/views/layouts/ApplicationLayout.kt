package kales.sample.app.views.layouts

import io.ktor.html.insert
import kales.actionview.ApplicationLayout
import kotlinx.html.*

class AppLayout : ApplicationLayout() {
  override fun HTML.apply() {
    head {
      title { +"Kales sample app" }
    }
    body {
      h1 { +"Kales sample app" }
      insert(body)
    }
  }
}
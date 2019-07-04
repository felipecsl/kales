package kales.sample.app.views.layouts

import io.ktor.html.insert
import kales.actionview.ApplicationLayout
import kotlinx.html.*

class AppLayout : ApplicationLayout() {
  override fun HTML.apply() {
    head {
      title { +"Kales Demo App" }
      link(
          rel = "stylesheet",
          href = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
      )
      styleLink("/assets/sampleapp.css")
    }
    body {
      div("container") {
        div("row") {
          div("col-sm-12") {
            h1("app-title") {
              a(href = "/") { +"Kales Demo App" }
            }
          }
        }
      }
      insert(body)
    }
  }
}

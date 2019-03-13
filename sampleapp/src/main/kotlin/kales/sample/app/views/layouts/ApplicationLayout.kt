package kales.sample.app.views.layouts

import io.ktor.html.insert
import kales.actionview.ApplicationLayout
import kotlinx.html.*

class AppLayout : ApplicationLayout() {
  override fun HTML.apply() {
    head {
      title { +"Kales sample app" }
      link(
          rel = "stylesheet",
          href = "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
      )
    }
    body {
      div("container") {
        div("row") {
          div("col-sm-12") {
            h1 {
              a(href = "/") { +"Kales sample app" }
            }
          }
        }
      }
      insert(body)
    }
  }
}

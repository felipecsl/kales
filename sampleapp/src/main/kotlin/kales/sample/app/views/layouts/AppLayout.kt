package kales.sample.app.views.layouts

import io.ktor.html.insert
import kales.actionpack.KalesApplicationCall
import kales.actionview.ApplicationLayout
import kotlinx.html.*

class AppLayout(call: KalesApplicationCall) : ApplicationLayout(call) {
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
      if (flash["notice"] != null) {
        div("container") {
          div("row") {
            div("col-sm-12") {
              div("notice") {
                +flash.getValue("notice").toString()
              }
            }
          }
        }
      }
      insert(body)
    }
  }
}

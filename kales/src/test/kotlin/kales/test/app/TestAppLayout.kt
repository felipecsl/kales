package kales.test.app

import io.ktor.html.insert
import kales.actionview.ApplicationLayout
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.title

class TestAppLayout : ApplicationLayout() {
  override fun HTML.apply() {
    head {
      title { +"Sample app" }
    }
    body {
      insert(body)
    }
  }
}
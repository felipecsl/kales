package kales

import com.google.common.truth.Truth.assertThat
import io.ktor.application.Application
import io.ktor.http.*
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kales.test.app.TestAppLayout
import kales.test.app.controllers.TestController
import org.junit.Test

class KalesApplicationIntegrationTest {
  @Test fun `test GET simple HTML view rendering with bindings`() {
    withTestApplication(Application::testModule) {
      with(handleRequest(HttpMethod.Get, "/")) {
        assertThat(HttpStatusCode.OK).isEqualTo(response.status())
        assertThat(response.content).isEqualTo("""
          <!DOCTYPE html>
          <html>
            <head>
              <title>Sample app</title>
            </head>
            <body>
              <h1>Hello foo</h1>
            </body>
          </html>

        """.trimIndent())
      }
    }
  }

  @Test fun `test POST with form params`() {
    withTestApplication(Application::testModule) {
      with(handleRequest(HttpMethod.Post, "/") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        setBody(listOf("message" to "hell0 w0r1d").formUrlEncode())
      }) {
        assertThat(HttpStatusCode.OK).isEqualTo(response.status())
        assertThat(response.content).isEqualTo("""
          <!DOCTYPE html>
          <html>
            <head>
              <title>Sample app</title>
            </head>
            <body>
              <h1>Posted: hell0 w0r1d</h1>
            </body>
          </html>

        """.trimIndent())
      }
    }
  }
}

fun Application.testModule() {
  kalesApp(TestAppLayout::class) {
    get<TestController>("/", "index")
    post<TestController>("/", "create")
  }
}

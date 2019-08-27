package kales

import com.google.common.truth.Truth.assertThat
import io.ktor.application.Application
import io.ktor.http.*
import io.ktor.server.testing.TestApplicationResponse
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
        assertSuccessfulResponseWithBody(response, "<h1>Hello foo</h1>")
      }
    }
  }

  @Test fun `test POST with form params`() {
    withTestApplication(Application::testModule) {
      with(handleRequest(HttpMethod.Post, "/") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        setBody(listOf("message" to "hell0 w0r1d").formUrlEncode())
      }) {
        assertSuccessfulResponseWithBody(response, "<h1>Posted: hell0 w0r1d</h1>")
      }
    }
  }

  @Test fun `test DELETE with _method parameter`() {
    withTestApplication(Application::testModule) {
      with(handleRequest(HttpMethod.Post, "/posts/3") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        setBody(listOf("_method" to "delete").formUrlEncode())
      }) {
        assertSuccessfulResponseWithBody(response, "<h1>Hello from destroy</h1>")
      }
    }
  }

  @Test fun `test PUT with _method parameter`() {
    withTestApplication(Application::testModule) {
      with(handleRequest(HttpMethod.Post, "/posts") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        setBody(listOf("_method" to "put").formUrlEncode())
      }) {
        assertSuccessfulResponseWithBody(response, "<h1>Hello putting</h1>")
      }
    }
  }

  @Test fun `test PATCH with _method parameter and extra params`() {
    withTestApplication(Application::testModule) {
      with(handleRequest(HttpMethod.Post, "/postes") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        setBody(listOf(
          "_method" to "patch",
          "foo" to "bar"
        ).formUrlEncode())
      }) {
        assertSuccessfulResponseWithBody(response, "<h1>Hello patchin' bar</h1>")
      }
    }
  }

  @Test fun `test PATCH with _method parameter, repeating URL and extra params`() {
    // This makes sure that we can call receiveParameters() from ApplicationCall multiple times for
    // the lifetime of a request. This is important because need to call that from the class
    // DynamicParameterRouteSelector when selecting a route and then later again when the controller
    // action is triggered.
    withTestApplication(Application::testModule) {
      with(handleRequest(HttpMethod.Post, "/posts") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        setBody(listOf(
          "_method" to "patch",
          "foo" to "baz"
        ).formUrlEncode())
      }) {
        assertSuccessfulResponseWithBody(response, "<h1>Hello patchin' baz</h1>")
      }
    }
  }

  @Test fun `responds with status 404 when view was not found`() {
    withTestApplication(Application::testModule) {
      with(handleRequest(HttpMethod.Get, "/should404")) {
        assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
      }
    }
  }

  private fun assertSuccessfulResponseWithBody(
    response: TestApplicationResponse,
    expectedContent: String
  ) {
    assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
    assertThat(response.content).isEqualTo("""
          <!DOCTYPE html>
          <html>
            <head>
              <title>Sample app</title>
            </head>
            <body>
              $expectedContent
            </body>
          </html>

        """.trimIndent())
  }
}

fun Application.testModule() {
  kalesApp(TestAppLayout::class) {
    get<TestController>("/", "index")
    get<TestController>("/should404", "actionWithoutView")
    post<TestController>("/", "create")
    delete<TestController>("/posts/{id}", "destroy")
    put<TestController>("/posts", "put")
    patch<TestController>("/postes", "patch")
    patch<TestController>("/posts", "patch")
  }
}

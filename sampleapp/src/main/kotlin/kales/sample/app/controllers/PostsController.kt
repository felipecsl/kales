package kales.sample.app.controllers

import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import kales.actionpack.ApplicationController
import kales.sample.app.models.Post
import kales.sample.app.views.posts.IndexViewModel
import kales.sample.app.views.posts.PostViewModel

@Suppress("unused")
class PostsController(call: ApplicationCall) : ApplicationController(call) {
  fun index() {
    bindings = IndexViewModel("Foo", Post.all())
  }

  fun show() {
    bindings = PostViewModel(Post.find(call.parameters["id"]?.toInt()
        ?: throw IllegalArgumentException("Missing parameter id")))
  }

  fun new() {
  }

  suspend fun create() {
    val params = call.receiveParameters()
    bindings = PostViewModel(Post.create(params["title"]!!, params["content"]!!))
  }
}
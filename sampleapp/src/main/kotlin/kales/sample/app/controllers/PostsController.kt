package kales.sample.app.controllers

import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import kales.actionpack.ApplicationController
import kales.sample.app.models.Comment
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
    val params = receiveParameters()
    bindings = PostViewModel(Post.create(params["title"]!!, params["content"]!!))
  }

  fun destroy() {
    // TODO: it's confusing that URL parameters are received differently than form params
    //  They should be consolidated into the same Parameters object by the superclass method
    //  receiveParameters()
    //
    // TODO: DestroyView not found returns HTTP 500
    //  RuntimeException: Unable to find view class kales.sample.app.views.posts.DestroyView
    //  That should probably return 404 instead
    //
    // TODO: We should be able to redirectTo another action at this point
    val params = call.parameters
    val post = Post.find(params["id"]!!.toInt())!!
    post.destroy()
  }

  suspend fun writeComment() {
    val params = receiveParameters()
    val commentText = params["comment_text"]
        ?: throw IllegalArgumentException("Missing param `comment_text`")
    val id = params["id"]?.toInt()
        ?: throw IllegalArgumentException("Missing param `id`")
    Comment.create(commentText, id)
    bindings = PostViewModel(Post.find(id))
  }
}
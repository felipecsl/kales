package kales.sample.app.controllers

import io.ktor.application.ApplicationCall
import io.ktor.request.receiveParameters
import io.ktor.response.respondRedirect
import kales.actionpack.ApplicationController
import kales.actionview.RedirectResult
import kales.sample.app.models.Comment
import kales.sample.app.models.Post
import kales.sample.app.views.posts.IndexView
import kales.sample.app.views.posts.IndexViewModel
import kales.sample.app.views.posts.PostViewModel

@Suppress("unused", "MemberVisibilityCanBePrivate")
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

  suspend fun update(): RedirectResult {
    val params = receiveParameters()
    val id = params["id"]?.toInt() ?: throw IllegalArgumentException("Missing param `id`")
    // TODO we should be able to construct a post object automatically from the params?
    //  Check what kind of magic Rails does in this situation.
    Post.find(id)
      ?.copy(title = params["post[title]"]!!, content = params["post[content]"]!!)
      ?.save()
    return redirectTo(::show)
  }

  suspend fun create() {
    val params = receiveParameters()
    bindings = PostViewModel(Post.create(params["title"]!!, params["content"]!!))
  }

  suspend fun destroy() {
    // TODO: We should be able to redirectTo another action at this point #71
    val params = receiveParameters()
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
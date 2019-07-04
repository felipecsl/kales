package kales.sample

import io.ktor.http.content.files
import io.ktor.http.content.static
import kales.KalesApplication
import kales.actionview.ApplicationLayout
import kales.sample.app.controllers.PostsController

fun <T : ApplicationLayout> routes(): KalesApplication<T>.() -> Unit = {
  // https://guides.rubyonrails.org/routing.html#crud-verbs-and-actions
  get<PostsController>("/", "index")
  get<PostsController>("/posts/new", "new")
  post<PostsController>("/posts", "create")
  get<PostsController>("/posts/{id}", "show")
  put<PostsController>("/posts/{id}", "update")
  delete<PostsController>("/posts/{id}", "destroy")
  post<PostsController>("/posts/{id}/comments", "writeComment")
}

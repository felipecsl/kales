package kales.sample.app.views.posts

import kales.actionpack.ViewModel
import kales.sample.app.models.Post

data class IndexViewModel(
  val name: String,
  val posts: List<Post>
) : ViewModel
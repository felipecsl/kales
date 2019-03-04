package kales.sample.app.views.posts

import kales.actionview.ViewModel
import kales.sample.app.models.Post

data class PostViewModel(
    val post: Post? = null
) : ViewModel
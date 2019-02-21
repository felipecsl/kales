package kales.sample.app.views.example

import kales.actionview.ViewModel
import kales.sample.app.models.Video

data class ShowViewModel(
    val video: Video? = null
) : ViewModel
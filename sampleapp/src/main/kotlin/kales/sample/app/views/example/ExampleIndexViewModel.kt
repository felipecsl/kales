package kales.sample.app.views.example

import kales.actionview.ViewModel
import kales.sample.app.models.Video

data class ExampleIndexViewModel(
    val name: String,
    val videos: List<Video>
) : ViewModel
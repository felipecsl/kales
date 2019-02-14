package krails.sample.controllers

import krails.actionpack.ApplicationController
import krails.actionview.ActionView
import krails.sample.views.example.ExampleIndexView
import kotlin.reflect.KClass

class ExampleController : ApplicationController() {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ActionView> index(): KClass<T>? {
    return ExampleIndexView::class as KClass<T>
  }
}
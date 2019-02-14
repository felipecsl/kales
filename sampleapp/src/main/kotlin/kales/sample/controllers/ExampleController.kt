package kales.sample.controllers

import kales.actionpack.ApplicationController
import kales.actionview.ActionView
import kales.sample.views.example.ExampleIndexView
import kotlin.reflect.KClass

class ExampleController : ApplicationController() {
  override fun <T : ActionView> index() =
      ExampleIndexView::class as KClass<T>
}
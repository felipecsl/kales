package krails.actionpack

import krails.actionview.ActionView
import kotlin.reflect.KClass

abstract class ApplicationController() {
  open fun <T : ActionView> index(): KClass<T>? = null

  open fun <T : ActionView> show(): KClass<T>? = null

  open fun <T : ActionView> create(): KClass<T>? = null

  open fun <T : ActionView> new(): KClass<T>? = null

  companion object {
    fun newInstance() {
    }
  }
}
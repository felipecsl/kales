package kales

import kales.actionpack.ApplicationController
import kotlin.reflect.KClass

data class RequestRoute<T : ApplicationController>(
  val controllerClass: KClass<T>,
  val method: String,
  val path: String,
  val action: String
)
package kales.internal

import kotlin.reflect.KProperty

fun <T> mutableLazy(initializer: () -> T) = Delegate(lazy(initializer))

class Delegate<T>(private val lazy: Lazy<T>) {
  private var value: T? = null

  operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
    return value ?: lazy.getValue(thisRef, property)
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
  }
}
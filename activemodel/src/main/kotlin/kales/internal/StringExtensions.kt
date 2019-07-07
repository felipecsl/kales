package kales.internal

fun String.singularize() =
  if (endsWith("s")) {
    this.substring(0, length - 1)
  } else {
    this
  }

fun String.pluralize() =
  if (!endsWith("s")) {
    "${this}s"
  } else {
    this
  }
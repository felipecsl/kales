package kales.actionview

import kales.ApplicationRecord
import kales.internal.KApplicationRecordClass
import kotlinx.html.*
import kotlinx.html.attributes.enumEncode

/** This enum mirrors `FormMethod` but without the deprecated annotations for put/delete/patch */
@Suppress("EnumEntryName")
enum class KalesFormMethod {
  get,
  post,
  put,
  delete,
  patch
}

val proxiedFormMethods = setOf(
  KalesFormMethod.put,
  KalesFormMethod.delete,
  KalesFormMethod.patch
)

@HtmlTagMarker
fun FlowContent.formFor(
  record: ApplicationRecord,
  method: KalesFormMethod = KalesFormMethod.post,
  encType: FormEncType? = null,
  classes: String? = null,
  block: FORM.() -> Unit = {}
) {
  val recordClasss = KApplicationRecordClass(record.javaClass.kotlin)
  val routeUrl = "/${recordClasss.tableName}/${record.id}"
  val finalBlock = if (proxiedFormMethods.contains(method)) {
    {
      input(type = InputType.hidden, name = "_method") { value = method.name }
      block()
    }
  } else {
    block
  }
  val formMethod = if (proxiedFormMethods.contains(method)) {
    FormMethod.post
  } else {
    FormMethod.valueOf(method.name)
  }
  FORM(attributesMapOf(
    "action", routeUrl,
    "enctype", encType?.enumEncode(),
    "method", formMethod.enumEncode(),
    "class", classes
  ), consumer).visit(finalBlock)
}
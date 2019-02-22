package kales.migrations.column

internal class DateTimeColumn(name: String) :
    AbstractDateTimeColumn(name) {
  override var sqlDefault: String? = null
}
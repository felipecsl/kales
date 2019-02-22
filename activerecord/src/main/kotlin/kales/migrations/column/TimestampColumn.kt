package kales.migrations.column

internal class TimestampColumn(name: String) :
    AbstractDateTimeColumn(name), TimeZoneInterface {
  override var withTimeZone = false
}
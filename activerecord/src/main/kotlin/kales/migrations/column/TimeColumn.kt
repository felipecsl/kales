package kales.migrations.column

import kales.migrations.AbstractColumn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

internal class TimeColumn(name: String) : AbstractColumn(name), TimeZoneInterface {
  private val formatter = DateTimeFormatter.ofPattern("H[H]:m[m][:s[s]][.SSS][ zzz]")
  var default: String?
    get() {
      return defaultLocalTime?.toString()
    }
    set(value) {
      defaultLocalTime = value?.let {
        LocalTime.parse(it, formatter)
      }
    }
  var defaultDate: Date?
    set(value) {
      defaultLocalTime = value?.let {
        LocalDateTime.ofInstant(
            it.toInstant(), ZoneId.systemDefault()
        ).toLocalTime()
      }
    }
    get() {
      return defaultLocalTime?.let {
        Date.from(
            it
                .atDate(LocalDate.now())
                .atZone(ZoneId.systemDefault()).toInstant())
      }
    }
  var defaultLocalTime: LocalTime? = null
    set(value) {
      field = value
      sqlDefault = value?.let { "'$it'" }
    }

  override var sqlDefault: String? = null

  override var withTimeZone = false
}
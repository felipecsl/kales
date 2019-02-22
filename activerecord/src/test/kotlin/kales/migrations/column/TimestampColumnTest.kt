package kales.migrations.column

import org.junit.Test
import kotlin.test.assertEquals

class TimestampColumnTest {
  @Test fun testInstanceVariable() {
    val timestampColumn = TimestampColumn("name")
    assertEquals("name", timestampColumn.name)
    assertEquals(false, timestampColumn.hasDefault)
    timestampColumn.default = "2018-02-13 11:12:13"
    assertEquals(true, timestampColumn.hasDefault)
    assertEquals("'2018-02-13T11:12:13'", timestampColumn.sqlDefault)
  }
}
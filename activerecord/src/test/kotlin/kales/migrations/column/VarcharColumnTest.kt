package kales.migrations.column

import org.junit.Test
import kotlin.test.assertEquals

class VarcharColumnTest {
  @Test fun testInstanceVariable() {
    val varcharColumn = VarcharColumn("name")
    assertEquals("name", varcharColumn.name)
    assertEquals(false, varcharColumn.hasDefault)
    varcharColumn.default = "text"
    assertEquals(true, varcharColumn.hasDefault)
    assertEquals("'text'", varcharColumn.sqlDefault)
  }
}
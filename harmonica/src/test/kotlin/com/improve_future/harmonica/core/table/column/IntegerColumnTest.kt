/*
 * Harmonica: Kotlin Database Migration Tool
 * Copyright (C) 2019  Kenji Otsuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.improve_future.harmonica.core.table.column

import kotlin.test.Test
import kotlin.test.assertEquals

class IntegerColumnTest {
  @Test
  fun testInstanceVariable() {
    val integerColumn = IntegerColumn("name")
    assertEquals("name", integerColumn.name)
    assertEquals(false, integerColumn.hasDefault)
    integerColumn.default = 1
    assertEquals(true, integerColumn.hasDefault)
    assertEquals("1", integerColumn.sqlDefault)
  }
}

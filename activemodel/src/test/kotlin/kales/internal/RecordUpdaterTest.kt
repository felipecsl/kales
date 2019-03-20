package kales.internal

import kales.ApplicationRecord.Companion.JDBI
import kales.Foo
import kales.TestModel
import kales.activemodel.use
import org.junit.Test

class RecordUpdaterTest {
  @Test(expected = IllegalArgumentException::class)
  fun `update record with mismatching klass should throw exception`() {
    JDBI.use {
      val updater = RecordUpdater(it, KApplicationRecordClass(TestModel::class))
      updater.update(Foo(0, ""))
    }
  }
}
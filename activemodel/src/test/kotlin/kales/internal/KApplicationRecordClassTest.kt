package kales.internal

import com.google.common.truth.Truth.assertThat
import kales.Foo
import kales.TestModel
import org.junit.Test

class KApplicationRecordClassTest {
  @Test fun foreignKeyColumnName() {
    assertThat(KApplicationRecordClass(TestModel::class).foreignKeyColumnName)
      .isEqualTo("testmodel_id")
    assertThat(KApplicationRecordClass(Foo::class).foreignKeyColumnName)
      .isEqualTo("foo_id")
  }

  @Test fun tableName() {
    assertThat(KApplicationRecordClass(TestModel::class).tableName)
      .isEqualTo("testmodels")
    assertThat(KApplicationRecordClass(Foo::class).tableName)
      .isEqualTo("foos")
  }
}
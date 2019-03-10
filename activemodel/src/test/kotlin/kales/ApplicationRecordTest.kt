package kales

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ApplicationRecordTest {
  @Test fun `test no records`() {
    withTestDb {
      assertThat(TestModel.all()).isEmpty()
      assertThat(TestModel.where("id" to 1)).isEmpty()
      assertThat(TestModel.find(1)).isNull()
      assertThat(TestModel.find(2)).isNull()
    }
  }

  @Test fun `test all`() {
    withTestDb {
      TestModel.create("name" to "Hello World")
      TestModel.create("name" to "Ping Pong")
      val expectedModel1 = TestModel(1, "Hello World")
      val expectedModel2 = TestModel(2, "Ping Pong")
      assertThat(TestModel.all()).containsExactly(expectedModel1, expectedModel2)
    }
  }

  @Test fun `test where`() {
    withTestDb {
      TestModel.create("name" to "Hello World")
      TestModel.create("name" to "Ping Pong")
      val expectedModel1 = TestModel(1, "Hello World")
      val expectedModel2 = TestModel(2, "Ping Pong")
      assertThat(TestModel.where("name" to "Hello World")).containsExactly(expectedModel1)
      assertThat(TestModel.where("id" to 1, "name" to "Hello World")).containsExactly(expectedModel1)
      assertThat(TestModel.where("id" to 1)).containsExactly(expectedModel1)
      assertThat(TestModel.where("id" to 2)).containsExactly(expectedModel2)
      assertThat(TestModel.where("id" to 3)).isEmpty()
    }
  }

  @Test fun `test create return value`() {
    withTestDb {
      val obj = TestModel.create("name" to "Hello World")
      assertThat(obj).isEqualTo(TestModel(1, "Hello World"))
    }
  }

  @Test fun `test find`() {
    withTestDb {
      TestModel.create("name" to "Hello World")
      TestModel.create("name" to "Ping Pong")
      assertThat(TestModel.find(1)).isEqualTo(TestModel(1, "Hello World"))
      assertThat(TestModel.find(2)).isEqualTo(TestModel(2, "Ping Pong"))
      assertThat(TestModel.find(3)).isNull()
    }
  }

  @Test fun `test associations`() {
    withTestDb {
      TestModel.create("name" to "Hello World")
      TestModel.create("name" to "Ping Pong")
      Foo.create()
      val expectedModel1 = TestModel(1, "Hello World")
      val expectedModel2 = TestModel(2, "Ping Pong")
      assertThat(TestModel.all()).containsExactly(expectedModel1, expectedModel2)
    }
  }

  private fun withTestDb(block: () -> Unit) {
    ApplicationRecord.JDBI.withHandle<Any, RuntimeException> {
      it.execute("CREATE TABLE testmodels (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR)")
      block()
    }
  }
}
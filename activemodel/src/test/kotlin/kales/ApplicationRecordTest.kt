package kales

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ApplicationRecordTest {
  @Test fun `no records`() {
    ApplicationRecord.JDBI.withHandle<Any, RuntimeException> {
      it.execute("CREATE TABLE testmodels (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR)")
      assertThat(TestModel.all()).isEmpty()
      assertThat(TestModel.where("id" to 1)).isEmpty()
      assertThat(TestModel.find(1)).isNull()
      assertThat(TestModel.find(2)).isNull()
    }
  }

  @Test fun `test model create and query`() {
    ApplicationRecord.JDBI.withHandle<Any, RuntimeException> {
      it.execute("CREATE TABLE testmodels (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR)")
      TestModel.create("name" to "Hello World")
      TestModel.create("name" to "Ping Pong")
      val expectedModel1 = TestModel(1, "Hello World")
      val expectedModel2 = TestModel(2, "Ping Pong")
      assertThat(TestModel.all()).containsExactly(expectedModel1, expectedModel2)
      assertThat(TestModel.where("name" to "Hello World")).containsExactly(expectedModel1)
      assertThat(TestModel.where("id" to 1)).containsExactly(expectedModel1)
      assertThat(TestModel.where("id" to 2)).containsExactly(expectedModel2)
      assertThat(TestModel.find(1)).isEqualTo(expectedModel1)
      assertThat(TestModel.find(2)).isEqualTo(expectedModel2)
    }
  }
}
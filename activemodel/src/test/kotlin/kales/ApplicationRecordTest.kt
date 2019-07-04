package kales

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ApplicationRecordTest {
  @Test fun `test no records`() {
    withTestDb {
      assertThat(TestModel.all()).isEmpty()
      assertThat(TestModel.where(id = 1)).isEmpty()
      assertThat(TestModel.find(1)).isNull()
      assertThat(TestModel.find(2)).isNull()
    }
  }

  @Test fun `test all`() {
    withTestDb {
      TestModel.create("Hello World")
      TestModel.create("Ping Pong")
      val expectedModel1 = TestModel(1, "Hello World")
      val expectedModel2 = TestModel(2, "Ping Pong")
      assertThat(TestModel.all()).containsExactly(expectedModel1, expectedModel2)
    }
  }

  @Test fun `test update model`() {
    withTestDb {
      val helloWorld = TestModel.create("Hello World")
      val updated = helloWorld.copy(name = "H3ll0 W0r1d")
      updated.save()
      assertThat(TestModel.find(helloWorld.id)!!).isEqualTo(updated)
    }
  }

  @Test fun `test where`() {
    withTestDb {
      TestModel.create("Hello World")
      TestModel.create("Ping Pong")
      val expectedModel1 = TestModel(1, "Hello World")
      val expectedModel2 = TestModel(2, "Ping Pong")
      assertThat(TestModel.where(name = "Hello World")).containsExactly(expectedModel1)
      assertThat(TestModel.where(id = 1, name = "Hello World")).containsExactly(expectedModel1)
      assertThat(TestModel.where(id = 1)).containsExactly(expectedModel1)
      assertThat(TestModel.where(id = 2)).containsExactly(expectedModel2)
      assertThat(TestModel.where(id = 3)).isEmpty()
    }
  }

  @Test fun `test create return value`() {
    withTestDb {
      val obj = TestModel.create("Hello World")
      assertThat(obj).isEqualTo(TestModel(1, "Hello World"))
    }
  }

  @Test fun `test find`() {
    withTestDb {
      TestModel.create("Hello World")
      TestModel.create("Ping Pong")
      assertThat(TestModel.find(1)).isEqualTo(TestModel(1, "Hello World"))
      assertThat(TestModel.find(2)).isEqualTo(TestModel(2, "Ping Pong"))
      assertThat(TestModel.find(3)).isNull()
    }
  }

  @Test fun `test one-to-many association`() {
    withTestDb {
      val pingPong = TestModel.create("Ping Pong")
      TestModel.create("Fla Flu")
      TestModel.create("Kit Kat")
      val blah = Foo.create("blah", pingPong)
      val bluh = Foo.create("bluh", pingPong)
      assertThat(TestModel.find(pingPong.id)!!.foos).containsExactly(blah, bluh)
    }
  }

  @Test fun `test destroy record`() {
    withTestDb {
      val pingPong = TestModel.create("Ping Pong")
      assertThat(TestModel.find(1)).isEqualTo(TestModel(1, "Ping Pong"))
      pingPong.destroy()
      assertThat(TestModel.find(1)).isNull()
    }
  }

  @Test fun `test many-to-one association`() {
    withTestDb {
      val pingPong = TestModel.create("Ping Pong")
      val blah = Foo.create("blah", pingPong)
      assertThat(Foo.find(blah.id)!!.testModel.value).isEqualTo(pingPong)
    }
  }

  @Test fun `test update has many association`() {
    withTestDb {
      val pingPong = TestModel.create("Ping Pong")
      val blah = Foo.create("blah")
      pingPong.foos.add(blah)
      pingPong.save()
      val updatedBlah = Foo.find(blah.id)!!
      assertThat(updatedBlah.testModel.value).isEqualTo(pingPong)
      assertThat(TestModel.find(pingPong.id)!!.foos).containsExactly(updatedBlah)
    }
  }

  @Test fun `test update belongs to association`() {
    withTestDb {
      val pingPong = TestModel.create("Ping Pong")
      val blah = Foo.create("blah")
      blah.testModel.value = pingPong
      blah.save()
      assertThat(Foo.find(blah.id)!!.testModel.value).isEqualTo(pingPong)
      assertThat(TestModel.find(pingPong.id)!!.foos).containsExactly(blah)
    }
  }

  private fun withTestDb(block: () -> Unit) {
    ApplicationRecord.JDBI.withHandle<Any, RuntimeException> {
      it.execute("CREATE TABLE testmodels (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR)")
      it.execute("CREATE TABLE foos (" +
          "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
          "foo VARCHAR, " +
          "testmodel_id INTEGER)")
      block()
    }
  }
}
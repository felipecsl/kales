package kales.migrations

import java.lang.reflect.Modifier

internal class StubMigration : AbstractMigration() {
  private val delegateAdapterField = AbstractMigration::class.java
      .getDeclaredField("adapter\$delegate").also {
        it.isAccessible = true
      }
  val adapter = StubDbAdapter()

  init {
    connection = StubConnection()
    delegateAdapterField.also { field ->
      field.isAccessible = true
      val modifier = field::class.java.getDeclaredField("modifiers")
      modifier.isAccessible = true
      modifier.setInt(field, field.modifiers and Modifier.FINAL.inv())
      field.set(this as AbstractMigration, lazyOf(adapter))
    }
  }
}
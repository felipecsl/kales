package kales.cli

/** Represents a Java package, g.: com.foo.bar */
internal class PackageName(private val stringRepresentation: String) {
  private val parts = stringRepresentation.split(".")

  /** Returns a new [PackageName] representing the parent package */
  val parentPackage =
      if (parts.size > 1) {
        PackageName(parts.slice(0..parts.size - 2).joinToString("."))
      } else {
        throw IllegalStateException("Unable to obtain parent package of '$this'")
      }


  fun childPackage(vararg parts: String) =
      PackageName("$stringRepresentation.${parts.joinToString(".")}")

  override fun toString() = stringRepresentation
}
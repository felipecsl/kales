package kales.cli

/** Represents a Java package, g.: com.foo.bar */
internal class PackageName private constructor(private val stringRepresentation: String) {
  private val parts = stringRepresentation.split(".")

  /** Returns a new [PackageName] representing the parent package */
  val parentPackage by lazy {
    if (parts.size > 1) {
      PackageName(parts.slice(0..parts.size - 2).joinToString("."))
    } else {
      throw IllegalStateException("Unable to obtain parent package of '$this'")
    }
  }

  fun isValid() = stringRepresentation.matches(VALID_PKG_REGEX.toRegex())

  fun childPackage(vararg parts: String) =
      PackageName("$stringRepresentation.${parts.joinToString(".")}")

  override fun toString() = stringRepresentation

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as PackageName

    if (stringRepresentation != other.stringRepresentation) return false

    return true
  }

  override fun hashCode(): Int {
    return stringRepresentation.hashCode()
  }

  companion object {
    private val VALID_PKG_REGEX = "^(?:\\w+|\\w+\\.\\w+)+\$".toPattern()

    /** Parses the provided [stringRepresentation] into a [PackageName] or throws if invalid */
    fun parse(stringRepresentation: String): PackageName {
      val pkg = PackageName(stringRepresentation)
      return if (!pkg.isValid()) {
        throw IllegalArgumentException("Invalid package name $stringRepresentation")
      } else {
        pkg
      }
    }
  }
}
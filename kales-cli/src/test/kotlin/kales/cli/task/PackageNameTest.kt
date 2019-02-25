package kales.cli.task

import com.google.common.truth.Truth.assertThat
import kales.cli.PackageName
import org.junit.Test
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class PackageNameTest {
  @Test fun `test parent package`() {
    assertThat(PackageName.parse("com.example").parentPackage).isEqualTo(PackageName.parse("com"))
  }

  @Test(expected = IllegalStateException::class) fun `no parent package`() {
    PackageName.parse("foo").parentPackage
  }

  @Test fun childPackage() {
    assertThat(PackageName.parse("com.example").childPackage("foo", "bar"))
        .isEqualTo(PackageName.parse("com.example.foo.bar"))
  }

  @Test(expected = IllegalArgumentException::class) fun `invalid input`() {
    PackageName.parse("hello w0rld$ :)")
  }
}
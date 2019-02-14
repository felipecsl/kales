# Kales

Let's see how hard it would be to create web framework 
like Ruby on Rails but in Kotlin. 
Kales run on top of [Ktor](ktor.io)

## Usage

Check the `sampleapp` directory for an application that uses
some of the features exposed by Kales.

`ExampleController.kt`
```$kotlin
class ExampleController : ApplicationController() {
  override fun <T : ActionView> index() = 
      ExampleIndexView::class as KClass<T>
}
```

`ExampleIndexView`
```kotlin
class ExampleIndexView(html: HTML) : ActionView(html) {
  override fun render() {
    html.head {
      title { +"Hello World" }
    }
    html.body {
      h1 {
        +"Title"
      }
      p {
        +"Hello from Kales"
      }
    }
  }
}
```

# License 

MIT
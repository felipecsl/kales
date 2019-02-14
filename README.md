# Kales

Let's see how hard it would be to create web framework 
like Ruby on Rails but in Kotlin. 
Kales run on top of [Ktor](https://ktor.io/).

## Running 

```
./gradlew sampleapp:run
```
then open `http://localhost:8080` on your browser.

## Usage

Check the `sampleapp` directory for an application that uses
some of the features exposed by Kales.

`ExampleController.kt`
```kotlin
class ExampleController : ApplicationController() {
  override fun index() = { html: HTML ->
    val bindings = ExampleIndexViewModel("Felipe")
    ExampleIndexView(html).render(bindings)
  }
}
```

`ExampleIndexView.kt`
```kotlin
class ExampleIndexView(html: HTML) : ActionView<ExampleIndexViewModel>(html) {
  override fun render(bindings: ExampleIndexViewModel?) {
    html.head {
      title { +"Kales sample app" }
    }
    html.body {
      h1 {
        +"Hello, ${bindings?.name}"
      }
      p {
        +"Greetings from Kales"
      }
    }
  }
}
```

# License 

MIT

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

#### Controller class
`ExampleController.kt`
```kotlin
class ExampleController : ApplicationController() {
  override fun index(): ActionView<*>? {
    val bindings = ExampleIndexViewModel("Felipe")
    return ExampleIndexView(bindings)
  }
}
```

#### View class
`ExampleIndexView.kt`
```kotlin
class ExampleIndexView(
    bindings: ExampleIndexViewModel?
) : ActionView<ExampleIndexViewModel>(bindings) {
  override fun render(content: FlowContent) {
    content.h2 {
      +"Hello, ${bindings?.name}"
    }
    content.p {
      +"Greetings from Kales"
    }
  }
}
```

#### View Model
`ExampleIndexViewModel.kt`
```kotlin
data class ExampleIndexViewModel(
    val name: String
) : ViewModel
```

#### Application layout class
`ExampleApplicationLayout.kt`
```kotlin
class ExampleApplicationLayout : ApplicationLayout() {
  override fun HTML.apply() {
    head {
      title { +"Kales sample app" }
    }
    body {
      h1 { +"Kales sample app" }
      insert(body)
    }
  }
}
```

#### Main function
`App.kt`
```kotlin
fun main() {
  embeddedServer(
      Netty, 8080,
      watchPaths = listOf("sampleapp"),
      module = {
        kalesApplication(ExampleApplicationLayout::class) {
          get("/", ExampleController::index)
          get("/foo", ExampleController::foo)
        }
      }
  ).start()
}
```

That's it! You're good to go!
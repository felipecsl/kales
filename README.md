# Kales

Let's see how hard it would be to create web framework 
like Ruby on Rails but in Kotlin. 
Kales run on top of [Ktor](https://ktor.io/).

It uses a Model-View-Controller architecture. Database access is
done via [JDBI](http://jdbi.org/) and configured from a `database.yml` resource file (similar to Rails).

## Running 

```
./gradlew sampleapp:run
```
then open `http://localhost:8080` on your browser.

## Usage

Check the `sampleapp` directory for an application that uses
some of the features exposed by Kales.

#### Model
`Video.kt`
```kotlin
data class Video(
    val id: Int,
    val title: String
) : ApplicationRecord() {
  companion object {
    // Returns all records in the table
    fun all() = allRecords<Video>()
  }
}
```

#### Controller
`ExampleController.kt`
```kotlin
class ExampleController : ApplicationController() {
  override fun index() =
      ExampleIndexView(ExampleIndexViewModel("Felipe", Video.all()))

  fun details() =
      ExampleIndexView(ExampleIndexViewModel("Details", listOf()))
}
```

#### View
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
    content.h3 { +"Videos" }
    content.ul {
      bindings?.videos?.forEach { v ->
        li {
          +v.title
        }
      }
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

#### Application layout
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

#### Main
`App.kt`
```kotlin
fun main() {
  embeddedServer(
      Netty, 8080,
      watchPaths = listOf("sampleapp"),
      module = {
        kalesApplication(ExampleApplicationLayout::class) {
          get("/", ExampleController::index)
          get("/details", ExampleController::details)
        }
      }
  ).start()
}
```

That's it! You're good to go!
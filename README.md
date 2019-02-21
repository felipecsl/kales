# Kales

A modern web framework built for developer productivity and safety.  
Kales run on top of [Ktor](https://ktor.io/) and uses a Model-View-Controller architecture. 

Database access is done via [JDBI](http://jdbi.org/) and configured from a `database.yml` resource 
file (similar to Rails).

More documentation coming soon!

## Usage

Kales comes with a command line application `kales-cli` that can generate most of the boilerplate
needed to bootstrap a new web app using Kales. More details about this coming soon!

## Running the example app

```
./gradlew sampleapp:run
```
then open `http://localhost:8080` on your browser.

## Download

```
implementation 'com.felipecsl.kales:kales:0.0.1-SNAPSHOT'`
```

Snapshots of the development version are available in
[Sonatype's `snapshots` repository](https://oss.sonatype.org/content/repositories/snapshots/).

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
    fun all() = allRecords<Video>()

    fun where(vararg clause: Pair<String, Any>) = whereRecords<Video>(clause.toMap())

    fun find(id: Int) = findRecord<Video>(id)
  }
}
```

#### Controller
`ExampleController.kt`
```kotlin
class ExampleController(call: ApplicationCall) : ApplicationController(call) {
  override fun index(): Any? {
    bindings = IndexViewModel("Felipe", Video.all())
    return null
  }

  override fun show(): Any? {
    bindings = ShowViewModel(Video.find(call.parameters["id"]?.toInt()
        ?: throw IllegalArgumentException("Missing parameter id")))
    return null
  }
}
```

#### View
`IndexView.kt`
```kotlin
class IndexView(
    bindings: IndexViewModel? = IndexViewModel("Unknown", listOf())
) : ActionView<IndexViewModel>(bindings) {
  override fun render(content: FlowContent) {
    content.apply {
      h2 {
        +"Hello, ${bindings?.name}"
      }
      p {
        +"Greetings from Kales"
      }
      h3 {
        +"Videos"
      }
      ul {
        bindings?.videos?.forEach { v ->
          li {
            +v.title
          }
        }
      }
    }
  }
}
```

#### View Model
`IndexViewModel.kt`
```kotlin
data class IndexViewModel(
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
          get<ExampleController>("/", "index")
          get<ExampleController>("/video/{id}", "show")
        }
      }
  ).start()
}
```

That's it! You're good to go!
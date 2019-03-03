---
id: hot-reloading
title: Hot Reloading
---
During development, it's very useful to have a tight feedback loop so you can see your changes as 
soon as you make them. Since, unlike Ruby, Kotlin is a compiled language, there is a small overhead
involved with viewing your changes as soon as you make them, but Ktor makes it pretty easy to do that.

Ktor calls this "Autoreload" and it pretty much has out-of-the box support for this. For more information,
please check the [Ktor docs on this topic](https://ktor.io/servers/autoreload.html).

To summarize, in practice all you need to do is to have an extra Gradle build process running in 
parallel with your Kales server process. To do that, open a new Terminal window and run;

```bash
$ ./gradlew -qt jar
```

This will run a Gradle [continuous build](https://blog.gradle.org/introducing-continuous-build) that
will monitor changes to your source code and automatically rebuild your project as soon as your changes
are saved. Then, go back to your browser and refresh the page, you should see the updates immediately.
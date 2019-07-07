# Kales

A modern web framework built for developer productivity and safety.  
Kales runs on top of [Ktor](https://ktor.io/) and uses an [MVC architecture](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller). 

## Usage

Run the following in your terminal, then follow the onscreen instructions.

```
curl https://raw.githubusercontent.com/felipecsl/kales/master/scripts/install -sSf | sh
```

This will install the command line application `kales`. It can generate the boilerplate
you need to bootstrap a new web app.

## Running the example app

```
./gradlew sampleapp:run
```
then open `http://localhost:8080` on your browser.

## Hot reloading

Ktor supports hot reloading out of the box, simply open a new terminal window and run:

```
./gradlew -t sampleapp:jar
```

## Deploying the docs website

```
$ cd website
$ GIT_USER=felipecsl \
 CURRENT_BRANCH=master \
 USE_SSH=true \
 yarn run publish-gh-pages
```

and it will automatically rebuild as you make new changes.

## Download

```
implementation 'com.felipecsl.kales:kales:0.0.5-SNAPSHOT'
```

Snapshots of the development version are available in
[Sonatype's `snapshots` repository](https://oss.sonatype.org/content/repositories/snapshots/).

## Usage

Check the [`sampleapp`](https://github.com/felipecsl/kales/tree/master/sampleapp/src/main/kotlin/kales/sample) 
directory for an application that uses some of the features exposed by Kales.

For more information, please check the [Kales website](https://kales.dev/).
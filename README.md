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

## Download

```
implementation 'com.felipecsl.kales:kales:0.0.1-SNAPSHOT'
```

Snapshots of the development version are available in
[Sonatype's `snapshots` repository](https://oss.sonatype.org/content/repositories/snapshots/).

## Usage

Check the `sampleapp` directory for an application that uses
some of the features exposed by Kales.

For more information, please check the [Kales website](https://kales.dev/).
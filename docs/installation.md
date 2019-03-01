---
id: installation
title: Installing Kales
---

Before you install Kales, you should check to make sure that your system has the proper prerequisites 
installed. These include Java and SQLite3.

Open up a command line prompt. On macOS open Terminal.app, on Windows choose "Run" from your Start 
menu and type 'cmd.exe'. Any commands prefaced with a dollar sign $ should be run in the command line. 
Verify that you have a current version of Java installed:


```bash
$ java -version
java version "1.8.0_151"
Java(TM) SE Runtime Environment (build 1.8.0_151-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.151-b12, mixed mode)
```

Kales requires Java version 1.8.0 or later. If the version number returned is less than that number, 
you'll need to install a fresh copy of Java.

A number of tools exist to help you quickly install Java on your system. You can check the 
[Oracle website](https://www.oracle.com/technetwork/java/javase/downloads/index.html) for download
links and more information.

To install Kales, first choose a directory where you want to have it installed. For Linux/MacOS, we'd
recommend `~/.kales`, however you can pick any directory you'd like:

```bash
$ mkdir ~/.kales && cd ~/.kales
$ curl https://raw.githubusercontent.com/felipecsl/kales/master/scripts/install -sSf | sh
```

This will:

* Download the Kales command line tool, extract and place it in your chosen installation directory
* Update your terminal configuration so it knows where to look for the `kales` executable

To verify that you have everything installed correctly, you should be able to run the following:

```bash
$ kales version
```

If it says something like `0.0.1-SNAPSHOT`, you are ready to continue.

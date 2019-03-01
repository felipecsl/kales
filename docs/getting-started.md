---
id: getting-started
title: Getting Started
sidebar_label: Getting Started
---

Kales is a [Kotlin](https://kotlinlang.org/) web framework for rapid prototyping inspired on 
[Ruby on Rails](https://rubyonrails.org/). It borrows many of its tried and true conventions like 
generators, migrations, MVC architecture, no boilerplate and high productivity that made Rails so 
successful. In this document we'll guide you through getting started with a new Kales project.

If you have no prior knowledge of Java or Kotlin, you might want to look into getting 
familiar with the language and the concepts behind these tools before you dive into Kales.

## Creating a new project

Kales comes with a number of tools called generators that are designed to make your development 
life easier by creating everything that's necessary to start working on a particular task. 
One of these is the new application generator, which will provide you with the foundation of a 
fresh Kales application so that you don't have to write it yourself.

If you've already installed Kales, you should have the `kales` command available in your terminal. 
To generate your application it, navigate to a directory where you have rights to create files 
and type this in your terminal window:

```bash
$ kales new com.example.blog
```

This will create a Kales application under the directory `com.example.blog`.
After you create the blog application, switch to its folder:

```bash
$ cd com.example.blog
```

The application directory has a number of auto-generated files and folders that make up the structure 
of a Kales application. Most of the work in this guide will happen in the 
`src/main/kotlin/com/example/blog/app` folder, which is where the controllers, models and views of
your application live.
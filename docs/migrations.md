---
id: migrations
title: Database Migrations
---

## Migration Overview

Quoting from the [Rails docs](https://edgeguides.rubyonrails.org/active_record_migrations.html) on
this topic: 

> Migrations are a convenient way to alter your database schema over time in a consistent
and easy way. 
You can think of each migration as being a new 'version' of the database. A schema starts off with 
nothing in it, and each migration modifies it to add or remove tables, columns, or entries. 
Active Record knows how to update your schema along this timeline, bringing it from whatever point 
it is in the history to the latest version. 

Kales migrations use a Kotlin DSL (provided by the [Harmonica](https://github.com/KenjiOhtsuka/harmonica)
library) so that you don't have to write SQL by hand, allowing your schema and changes to be database
independent.

Here's an example of a migration:

```kotlin
class CreatePostsMigration : Migration() {
  override fun up() {
    createTable("posts") {
      varchar(columnName = "title", nullable = false)
      text(columnName = "content", nullable = true)
    }
  }

  override fun down() {
    dropTable("posts")
  }
}
```

This migration adds a table called `posts` with a `varchar` column called `title` and a text column
called `content`. A primary key column called id will also be added implicitly, as it's the default 
primary key for all Active Record models.

Note that we define the change that we want to happen moving forward in time. Before this migration 
is run, there will be no table. After, the table will exist. Active Record knows how to reverse this 
migration as well: if we roll this migration back, it will remove the table by running the `down`
method.

## Creating a Migration

Migrations are stored as files in the `db/migrate` directory, one for each migration class. The name 
of the file is of the form `MYYYYMMDDHHMMSS_CreatePosts.kt`, that is to say a UTC timestamp identifying 
the migration followed by an underscore followed by the name of the migration. Kales uses this 
timestamp to determine which migration should be run and in what order, so if you're copying a 
migration from another application or generate a file yourself, be aware of its position in the order.

Of course, calculating timestamps is no fun, so Kales provides a generator to handle making it for you:

```bash
kales generate migration CreatePosts
```

This will create an appropriately named empty migration:

```kotlin
class CreatePostsMigration : Migration() {
  override fun up() {
  }

  override fun down() {
  }
}
```

## Running Migrations

The very first migration related kales command you will use will probably be `kales db:migrate`. 
It just runs the `up` method for all the migrations that have not yet been run. If there are no such 
migrations, it exits. It will run these migrations in order based on the date of the migration.
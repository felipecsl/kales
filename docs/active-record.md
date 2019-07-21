---
id: active-record
title: Active Record Basics
---

## What is Active Record?

Quoting from the [Rails docs](https://guides.rubyonrails.org/active_record_basics.html) on
this topic: 

> Active Record is the M in MVC - the model - which is the layer of the system responsible 
 for representing business data and logic. Active Record facilitates the creation and use of 
 business objects whose data requires persistent storage to a database. It is an 
 implementation of the Active Record pattern which itself is a description of an Object 
 Relational Mapping system.

## Convention over Configuration

Just like Rails, Active Record in Kales relies heavily in conventions that can be used
interchangeably. That means, if you already know how Rails works, chances are 
you'll _get_ Kales very quickly too, so you don't need to re-learn everything. If you're
not familiar with Rails, that's fine too, as Kales provides a simple API that's follows 
a _batteries included_ approach. Again quoting the 
[Rails docs](https://guides.rubyonrails.org/active_record_basics.html#convention-over-configuration-in-active-record):

> When writing applications using other programming languages or frameworks, it may be 
 necessary to write a lot of configuration code. This is particularly true for ORM 
 frameworks in general. However, if you follow the conventions adopted by Rails, you'll 
 need to write very little configuration (in some cases no configuration at all) when 
 creating Active Record models. The idea is that if you configure your applications in 
 the very same way most of the time then this should be the default way. Thus, explicit 
 configuration would be needed only in those cases where you can't follow the standard 
 convention.

### Naming conventions

By default, Active Record uses some naming conventions to find out how the mapping between 
models and database tables should be created. Kales will pluralize your class names to find 
the respective database table. So, for a class `Book`, you should have a database table 
called **books**.
Kales is [not yet capable](https://github.com/felipecsl/kales/issues/49) of pluralizing (or 
singularizing) irregular words neither converting them to 
[camelCase](https://github.com/felipecsl/kales/issues/50).

### Schema conventions

Active Record uses naming conventions for the columns in database tables, depending on the 
purpose of these columns.

* Foreign keys - These fields should be named following the pattern
`singularized_table_name_id` (e.g., `item_id`, `order_id`). These are the fields that 
Active Record will look for when you create associations between your models.
* Primary keys - By default, Active Record will use an integer column named `id` as the 
table's primary key. When using Active Record Migrations to create your tables, this column
will be automatically created.

## Creating models

When creating Active Record models, you can either manually create a the class under your application's
`app/models` package or run the `kales generate model <ModelName>` command. Either way, you should
have a class that looks somewhat like this:

```kotlin
package com.example.app.models

data class Product(val id: MaybeRecordId, val name: String) : ApplicationRecord() {
  fun save() = saveRecord()
  fun destroy() = destroyRecord()

  companion object {
      fun all() = allRecords<Product>()
      fun find(id: MaybeRecordId) = findRecord<Product>(id)
  }
}
``` 

This will create a `Product` model, mapped to a `products` table at the database. By doing this you'll 
also have the ability to map the columns of each row in that table with the attributes of the 
instances of your model.

All records have an `id` column with type `MaybeRecordId`. This data type that represents 
`ApplicationRecord.id` and can be either `NoneId` or `RecordId` to represent the fact that an ID may 
or may not exist depending on whether the record has been previously persisted to the DB or not.

## Reading and Wrinting data

Kales currently exposes a (very basic) API for manipulating data. Over time more methods will be 
added to closely match what Rails can do. For now, the folowing basic scenarios are supported:

### Create

**TODO**: The `create` record API is still being worked out.
It is [not yet possible](https://github.com/felipecsl/kales/issues/51) to create a record using the 
`save` API.

### Read

**TODO**: The `where` record API that allows querying records by property name is still being worked out.

```kotlin
// return a collection with all products
val products = Product.all()
```

```kotlin
// return the product with ID=1 or null if none found
val product = Product.find(1)
```

### Update

```kotlin
// create product then update its name
val product = Product(1, "Beautiful Mug")
product.save()
val updated = product.copy(name = "Ugly Mug")
updated.save()
```

### Delete

```kotlin
// create then delete a record 
val product = Product(1, "Beautiful Mug")
product.save()
product.destroy()
```
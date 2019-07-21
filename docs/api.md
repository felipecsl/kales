---
id: api
title: API
---

## API Documentation

### [actionmailer](/api/actionmailer)

Action Mailer allows you to send emails from your application using mailer classes and views. 
Mailers work very similarly to controllers.

### [actionpack](/api/actionpack)

Action Pack is a framework for handling and responding to web requests. It provides mechanisms for 
routing (mapping request URLs to actions), defining controllers that implement actions, and 
generating responses by rendering views, which are templates of various formats. In short, Action 
Pack provides the view and controller layers in the MVC paradigm.

### [actionview](/api/actionview)

Action View is a framework for handling view template lookup and rendering, and provides view helpers 
that assist when building HTML forms and more.

### [activejob](/api/activejob)

TODO 
### [activemodel](/api/activemodel)

Active Model provides a known set of interfaces for usage in model classes. They allow for Action 
Pack helpers to interact with non-Active Record models, for example.

### [activerecord](/api/activerecord)

Active Record connects classes to relational database tables to establish an almost zero-configuration 
persistence layer for applications. The library provides a base class that, when subclassed, sets up 
a mapping between the new class and an existing table in the database. In the context of an 
application, these classes are commonly referred to as models. Models can also be connected to other 
models; this is done by defining associations.

Active Record relies heavily on naming in that it uses class and association names to establish 
mappings between respective database tables and foreign key columns. Although these mappings can be 
defined explicitly, it's recommended to follow naming conventions, especially when getting started 
with the library.

### [activesupport](/api/activesupport)

TODO 

### [kales](/api/kales)

Kales is the main entry point artifact for using Kales in your project. It connects all other pieces
together and is typically the artifact you'd add to your project's `dependencies` list.

### [kales-cli](/api/kales-cli)

kales-cli is responsible for managing the `kales` command line interface. it provides generators that
help you bootstrap your Kales application more efficiently whitout having to write tons of boilerplate.
---
id: controllers-overview
title: Controllers Overview
sidebar_label: Controllers Overview
---

## What is a Controller?

Again, quoting from the [Rails doc](https://guides.rubyonrails.org/action_controller_overview.html) on the topic:

> Action Controller is the C in MVC. After the router has determined which controller to use for a request, the controller is responsible for making sense of the request, and producing the appropriate output. Luckily, Action Controller does most of the groundwork for you and uses smart conventions to make this as straightforward as possible.<br><br>
  For most conventional RESTful applications, the controller will receive the request (this is invisible to you as the developer), fetch or save data from a model and use a view to create HTML output. If your controller needs to do things a little differently, that's not a problem, this is just the most common way for a controller to work.<br><br>
  A controller can thus be thought of as a middleman between models and views. It makes the model data available to the view so it can display that data to the user, and it saves or updates user data to the model.

A controller is a class which inherits from `ApplicationController` and has methods just like any 
other class. When your application receives a request, the routing will determine which controller 
and action to run, then Kales creates an instance of that controller and runs the method with the 
same name as the action.

> TODO document controller parameters 

> TODO document controller return values/view rendering 


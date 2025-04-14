# Easy-to-go one-off events (a.k.a. Effects) and even more :fire:

[![Maven Central](https://img.shields.io/maven-central/v/com.elveum/effects-core.svg?label=Maven%20Central)](https://uandcode.com/sh/effects)
![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)
![JDK](https://img.shields.io/badge/JDK-17-brightgreen.svg?style=flat)
![Android Studio](https://img.shields.io/badge/Android%20Studio-Meerkat-brightgreen.svg?style=flat)

> In general, the library enables components with longer lifecycles to interact 
> with those that have shorter lifecycles, without causing memory leaks.

The most common use case is sending one-off events from Android ViewModels to the UI.
With this library, there's no need for `SharedFlow`, `Channel`, or extra event-related 
properties in your state classes.

:warning: This page covers the 2.x release, which is currently in alpha. 
Looking for the stable 1.x version? Click [here](/README.md) to learn more.

Supported DI Frameworks:
- Hilt
- Koin
- Projects without any DI Framework

## Table of Contents

- [Basic example (One-off event with Hilt)](#basic-example-one-off-event-with-hilt)
- [Example projects](#example-projects)
- [Prerequisites](#prerequisites)
- [Installation (Single-module Projects)](#installation-single-module-projects) 
  - [Hilt Integration](#hilt-integration)
  - [Koin Integration](#koin-integration)
  - [Without a DI Framework](#without-a-di-framework)
- [Installation for Multi-Module Projects](#installation-for-multi-module-projects)
- [Migration from Version 1.x to 2.x](#migration-from-version-1x-to-2x)
- [Key Concepts (regardless of used DI framework)](#key-concepts-regardless-of-used-di-framework)
- [Detailed Tutorials](#detailed-tutorials)
  - [Effects + Hilt](#effects--hilt)
  - [Effects + Koin](#effects--koin)
  - [Effects Without a DI Framework](#effects-without-a-di-framework)

## Basic example (One-off event with Hilt)

Let's say you want to:
- Trigger navigation commands
- Show an alert dialog and capture the user's choice
- Display toasts, snackbars, etc.
- Subscribe to onClick events
- Safely access an Activity from anywhere in your code

1. Define an interface:

   ```kotlin
   interface Toasts {
       fun show(message: String)
   }
   ```

2. Inject the interface to a view-model constructor or to any other component
   that has longer lifecycle:

   ```kotlin
   @HiltViewModel
   class CatsViewModel @Inject constructor(
       // inject your interface here:
       val toasts: Toasts,
   ): ViewModel() {

       fun removeCat(cat: Cat) {
           // execute removal logic here;
           // ...
           // and initiate one-off event:
           toasts.show("Cat $cat has been removed")
       }

   }
   ```

3. Implement the interface and annotate the implementation with `@HiltEffect`.
   As a result, you can safely use an activity reference or any other UI-related
   stuff in the implementation:

   ```kotlin
   @HiltEffect // <-- do not forget this annotation
   class ToastsImpl(
       // you can add any UI-related stuff to the constructor without memory leaks
       private val activity: ComponentActivity,
   ): Toasts {

       override fun show(message: String) {
           Toasts.makeText(activity, message, Toast.LENGTH_SHORT).show()
       }

   }
   ```

4. Use the implementation either in @Composable functions or in Activity/Fragment classes :

   - for projects with Jetpack Compose:

     ```kotlin
     @AndroidEntryPoint
     class MainActivity: AppCompatActivity() {
 
         override fun onCreate(savedInstanceState: Bundle?) {
             super.onCreate()
             setContent {
                 // create an effect implementation
                 val toastsImpl = remember { ToastsImpl(this) }
                 // connect the effect implementation to an interface injected to
                 // a view-model constructor:
                 EffectProvider(toastsImpl) {
                     MyApp()
                 }
             }
         }
     }
      
     @Composable
     fun MyApp() {
         // optionally you can use getEffect() call to get an instance 
         // of the effect implementation class
         val toastsImpl = getEffect<ToastsImpl>()
         // or: 
         val toasts = getEffect<Toasts>()
     }
     
     ```

   - for projects without Jetpack Compose:

     ```kotlin
     @AndroidEntryPoint
     class MainActivity: AppCompatActivity() {
         private val toasts by lazyEffect { ToastsImpl(this) }         
     }
     ```

## Example projects

- Single-module apps:
  - [Hilt DI Framework](/app-examples/hilt/app-singlemodule)
  - [Koin DI Framework](/app-examples/koin/app-singlemodule)
  - [Project without DI frameworks](/app-examples/core/app-singlemodule)
- Multi-module apps:
  - [Hilt DI Framework](/app-examples/hilt/app-multimodule)
  - [Koin DI Framework](/app-examples/koin/app-multimodule)
  - [Project without DI frameworks](/app-examples/core/app-multimodule)

## Prerequisites

1. Use the latest version of Android Studio.
2. Use __Kotlin v2.0__ or above.
3. Ensure the [KSP](https://kotlinlang.org/docs/ksp-overview.html) plugin is added to your project. [How to install KSP?](/docs/ksp-installation.md).

    - The library can also be used without KSP. In that case, it behaves similarly to Retrofit, by 
      creating proxies at runtime. This approach has its own pros and cons, but if you're really interested 
      in using the library without KSP, check out [this guide](/docs/no-ksp-installation.md) for more details.
    
    - :warning: Note: Hilt requires KSP anyway.

4. Make sure your chosen DI framework is properly set up in your project.
   Guides for the most popular DI setups:
    1. [Hilt](/docs/ksp-and-hilt-installation.md).
    2. [Koin](/docs/ksp-and-koin-installation.md).
    3. Projects without any DI framework are also supported.

## Installation (Single-module Projects)

Installation steps may vary depending on the DI framework you're using.

### Hilt Integration

1. Add [Hilt and KSP](/docs/ksp-and-hilt-installation.md) to your Android project.
2. Add the following dependencies:

   ```kotlin
   // annotation processor (required):
   ksp("com.uandcode:effects2-hilt-compiler:2.0.0-alpha01")
   // for projects without Jetpack Compose:
   implementation("com.uandcode:effects2-hilt:2.0.0-alpha01")
   // for projects with Jetpack Compose:
   implementation("com.uandcode:effects2-hilt-compose:2.0.0-alpha01")
   ```

For more details, check out the [single-module Hilt example app](/app-examples/hilt/app-singlemodule).

### Koin Integration

1. Add [KSP](/docs/ksp-installation.md) to your project (recommended).
2. Add [Koin](/docs/ksp-and-koin-installation.md) to the project.
3. Add the following dependencies:

   ```kotlin
   // annotation processor:
   ksp("com.uandcode:effects2-koin-compiler:2.0.0-alpha01")
   // for projects without Jetpack Compose:
   implementation("com.uandcode:effects2-koin:2.0.0-alpha01")
   // for projects with Jetpack Compose:
   implementation("com.uandcode:effects2-koin-compose:2.0.0-alpha01")
   ```

Check out [single-module Koin example app](/app-examples/koin/app-singlemodule) for more details.

### Without a DI Framework

> While the library supports projects without a DI framework, we strongly recommend 
> using one, especially for medium to large codebases, as it can greatly improve
> scalability and maintainability.

1. Add [KSP](/docs/ksp-installation.md) to your project (recommended).
2. Add the following dependencies:

   ```kotlin
   // annotation processor:
   ksp("com.uandcode:effects2-core-compiler:2.0.0-alpha01")
   // for projects without Jetpack Compose:
   implementation("com.uandcode:effects2-core-lifecycle:2.0.0-alpha01")
   // for projects with Jetpack Compose:
   implementation("com.uandcode:effects2-core-compose:2.0.0-alpha01")
   ```

Check out [the single-module No-DI example app](/app-examples/core/app-singlemodule) for a working setup.

## Installation for Multi-Module Projects

- Dependencies for your __application module__ remain the same:

  ```kotlin
  // Hilt Integration:
  ksp("com.uandcode:effects2-hilt-compiler:2.0.0-alpha01")
  implementation("com.uandcode:effects2-hilt:2.0.0-alpha01") // without Jetpack Compose
  implementation("com.uandcode:effects2-hilt-compose:2.0.0-alpha01") // with Jetpack Compose
  
  // Koin Integration:
  ksp("com.uandcode:effects2-koin-compiler:2.0.0-alpha01")
  implementation("com.uandcode:effects2-koin:2.0.0-alpha01") // without Jetpack Compose
  implementation("com.uandcode:effects2-koin-compose:2.0.0-alpha01") // with Jetpack Compose
  
  // No DI:
  ksp("com.uandcode:effects2-core-compiler:2.0.0-alpha01")
  implementation("com.uandcode:effects2-core-lifecycle:2.0.0-alpha01") // without Jetpack Compose
  implementation("com.uandcode:effects2-core-compose:2.0.0-alpha01") // with Jetpack Compose
  ```

- Additional configuration is required for your __Library__ modules, if you
  plan to use any of the following annotations within library code:
  - `@HiltEffect` (Hilt extension)
  - `@KoinEffect` (Koin extension)
  - `@EffectClass`

- Steps for Library Modules
  - Ensure [KSP is added and configured](/docs/ksp-installation.md) in the library module.
  - Add the following KSP argument:
    
    ⚠️ This is required only in library modules, not in the application module

    ```kotlin
    // my-android-lib/build.gradle.kts:
    ksp {
        arg("effects.processor.metadata", "generate")
    }
    ```

- Explore example projects based on your DI setup:
  - [Hilt Multi-module App](/app-examples/hilt/app-multimodule)
  - [Koin Multi-module App](/app-examples/koin/app-multimodule)
  - [Multi-module App without DI frameworks](/app-examples/core/app-multimodule)

## Migration from Version 1.x to 2.x

The previous major version of the library supported only the Hilt DI Framework,
so this guide is relevant only for projects using Hilt.

1. In your `build.gradle` file, update the library dependencies:

   ```kotlin
   ksp("com.uandcode:effects2-hilt-compiler:2.0.0-alpha01")
   implementation("com.uandcode:effects2-hilt:2.0.0-alpha01") // without Jetpack Compose
   implementation("com.uandcode:effects2-hilt-compose:2.0.0-alpha01") // with Jetpack Compose
   ```

2. Update relevant import statements and package references:

   ```kotlin
   // lazyEffect delegate:
   import com.uandcode.effects.hilt.lazyEffect
   
   // EffectProvider:
   import com.uandcode.effects.hilt.compose.EffectProvider

   // EffectController:
   import com.uandcode.effects.core.EffectController
   
   // BoundEffectController:
   import com.uandcode.effects.core.BoundEffectController
   ```

3. Configure KSP in Multi-module projects.
   
   For __library modules__ only (not the application module), add the following KSP configuration 
   in each `build.gradle.kts` file::

   ```kotlin
   // my-android-lib/build.gradle.kts:
   ksp {
      arg("effects.processor.metadata", "generate")
   }
   ```

## Key Concepts (regardless of used DI framework)

Learn the fundamentals of how the library works:

👉 [Key Concepts](/docs/key-concepts.md)

## Detailed Tutorials

The library offers a consistent API across supported DI frameworks.
However, there may be small differences, such as package names, annotations, 
or setup steps.

### Effects + Hilt

Read more about integrating Effects with __Hilt__:

👉 [Effects + Hilt guide](/docs/effects-and-hilt.md)

### Effects + Koin

Read more about integrating Effects with Koin:

👉 [Effects + Koin guide](/docs/effects-and-koin.md)

### Effects Without a DI Framework

Using the library without any DI framework? Start here:

👉 [Pure Effects (No DI) Guide](/docs/effects-pure.md)

## Advanced Details

Get ready to learn more? Here is a dedicated page for you:
👉 [Advanced Details](/docs/advanced-concepts.md)

# Pure Effects (No DI) Guide

This page explains how to install and use the library without relying on DI frameworks.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation (Single-module Projects)](#installation-single-module-projects)
- [Installation for Multi-Module Projects](#installation-for-multi-module-projects)
- [Usage Example](#usage-example)
- [Custom Effect Scopes](#custom-effect-scopes)
- [Example Apps](#example-apps)

## Prerequisites

1. Use the latest version of Android Studio.
2. Use __Kotlin v2.0__ or higher.
3. Recommended: ensure the [KSP](https://kotlinlang.org/docs/ksp-overview.html) plugin is added to your project. [How to install KSP?](/docs/ksp-installation.md).

   The library can also be used without KSP. In this case, it behaves similarly to 
   Retrofit by creating proxies at runtime. This approach has its own set of pros and
   cons. If you're interested in using the library without KSP, check out [this guide](/docs/no-ksp-installation.md) 
   for more details.

## Installation (Single-module Projects)

Add the necessary dependencies:

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
  ksp("com.uandcode:effects2-core-compiler:2.0.0-alpha01")
  implementation("com.uandcode:effects2-core-lifecycle:2.0.0-alpha01") // without Jetpack Compose
  implementation("com.uandcode:effects2-core-compose:2.0.0-alpha01") // with Jetpack Compose
  ```

- Additional configuration is required for your __Library__ modules, if you
  plan to use `@EffectClass` annotation within the library code.

- Steps for Library Modules:
  - Ensure [KSP is added and configured](/docs/ksp-installation.md) in the library module.
  - Add the following KSP argument:

  ⚠️ This is required only in library modules, not in the application module

  ```kotlin
  // my-android-lib/build.gradle.kts:
  ksp {
      arg("effects.processor.metadata", "generate")
  }
  ```

- Explore an example Multi-module project without DI frameworks [here](/app-examples/core/app-multimodule).

## Usage Example

1. Define one or more effect interfaces:

   ```kotlin
   interface MyEffects {
       // simple effect (one-off event)
       fun launchCatDetails(cat: Cat)
       // effect which can return a result
       suspend fun showAlertDialog(message: String): Boolean
       // effect which can return an infinite number of results
       fun listenClicks(): Flow<String>
   }
   ```

2. Inject the interface into a view-model constructor using `RootEffectScopes`:

   ```kotlin
   class CatsViewModel(
       val myEffects: MyEffects = RootEffectScopes.global.getProxy()
   ): ViewModel() {

       fun onCatChosen(cat: Cat) {
           viewModelScope.launch {
               // example of using the effect which can return a result
               val confirmed = myEffects.showAlertDialog(
                   message = "Are you sure you want to open details screen?"
               )
               if (confirmed) {
                   // example of sending the one-off event 
                   myEffects.launchCatDetails(cat)
               }
           }
       }

   }
   ```

3. Implement the interface and annotate the implementation with `@EffectClass`.
   You can safely use references to activities or other UI-related components within the implementation:

   ```kotlin
   @EffectClass // <-- do not forget this annotation
   class MyEffectsImpl(
       // you can add any UI-related stuff to the constructor without memory leaks
       private val activity: ComponentActivity,
   ): MyEffects {

       override fun launchCatDetails(cat: Cat) {
           // use an activity reference for launching a new page (via fragment manager,
           // navigation component, etc.)
       }

       override suspend fun showAlertDialog(message: String): Boolean {
           // suspend function is automatically cancelled when activity is stopped
           // and then executed again when activity is started
           return suspendCancellableCoroutine { continuation ->
               //
               // show alert dialog here; use 'continuation' to send a user choice
               //
               continuation.invokeOnCancellation {
                   // cancel alert dialog here, e.g.:
                   dialog.dismiss()
               }
           }
       }

       override fun listenClicks(): Flow<String> {
           // flow is automatically cancelled when activity is stopped
           // and then executed again when activity is started if the flow hasn't been completed yet
           return callbackFlow { // channelFlow { ... } and flow { ... } can be used too
             // do something here
           }
       }
   }
   ```

4. Use the implementation either in `@Composable` functions or in Activity/Fragment classes:

   - for projects with Jetpack Compose:

     ```kotlin
     class MainActivity : AppCompatActivity() {

         override fun onCreate(savedInstanceState: Bundle?) {
             super.onCreate()
             setContent {
                 // create an effect implementation
                 val effectImpl = remember { MyEffectsImpl(this) }
                 // connect the effect implementation to an interface injected to
                 // a view-model constructor:
                 EffectProvider(effectImpl) {
                     MyApp()
                 }
             }
         }
     }
     
     @Composable
     fun MyApp() {
         // you can use getEffect() call to get an instance 
         // of effect implementation class
         val myEffectsImpl = getEffect<MyEffectsImpl>()
         // or: 
         val myEffects = getEffect<MyEffects>()
     }
     
     ```

   - for projects without Jetpack Compose:

     ```kotlin
     class MainActivity: AppCompatActivity() {
         private val myEffects by lazyEffect {
             MyEffectsImpl(this)
         }
     }
     ```

## Custom Effect Scopes

You can manually create children of existing `EffectScope` instances and manage
their lifecycle manually. A child scope inherits all effects from a parent scope
automatically.

```kotlin
val childEffectScope = RootEffectScope.empty.createChild(
   managedInterfaces = ManagedInterfaces.ListOf(MyEffect1::class, MyEffect2::class),
)

val grandChildEffectScope = childEffectScope.createChild(
   managedInterfaces = ManagedInterfaces.ListOf(MyEffect3::class),
)
```

## Example Apps

Check out [example apps](/app-examples/core) for more details ;)

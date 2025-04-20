# Effects + Koin guide

This page explains how to install and use the library with the Koin DI Framework.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation (Single-module Projects)](#installation-single-module-projects)
- [Installation for Multi-Module Projects](#installation-for-multi-module-projects)
- [Usage Example](#usage-example)
- [Koin Scopes and Effects](#koin-scopes-and-effects)
- [Using Effect Controllers](#using-effect-controllers)
- [Example Apps](#example-apps)

## Prerequisites

1. Use the latest version of Android Studio.
2. Use __Kotlin v2.0__ or above.
3. Ensure the [KSP](https://kotlinlang.org/docs/ksp-overview.html) plugin is added to your project. [How to install KSP?](/docs/ksp-installation.md).
   
   The library can also be used without KSP. In that case, it behaves similarly to Retrofit, by
   creating proxies at runtime. This approach has its own pros and cons, but if you're really interested
   in using the library without KSP, check out [this guide](/docs/no-ksp-installation.md) for more details.

4. Make sure [Koin](https://insert-koin.io/docs/setup/koin) is properly set up in your project. [How to install Koin?](/docs/ksp-and-koin-installation.md)

## Installation (Single-module Projects)

1. Add [Koin and KSP](/docs/ksp-and-koin-installation.md) to your Android project.
2. Add the following dependencies:

   ```kotlin
   // annotation processor:
   ksp("com.uandcode:effects2-koin-compiler:2.0.0-alpha03")
   // for projects without Jetpack Compose:
   implementation("com.uandcode:effects2-koin:2.0.0-alpha03")
   // for projects with Jetpack Compose:
   implementation("com.uandcode:effects2-koin-compose:2.0.0-alpha03")
   
   // Koin dependencies
   implementation("io.insert-koin:koin-android:4.0.3")
   // or:
   implementation("io.insert-koin:koin-androidx-compose:4.0.3")
   ```

For more details, check out the [single-module Koin example app](/app-examples/koin/app-singlemodule).

## Installation for Multi-Module Projects

- Dependencies for your __application module__ remain the same:

  ```kotlin
  ksp("com.uandcode:effects2-koin-compiler:2.0.0-alpha03")
  implementation("com.uandcode:effects2-koin:2.0.0-alpha03") // without Jetpack Compose
  implementation("com.uandcode:effects2-koin-compose:2.0.0-alpha03") // with Jetpack Compose
  ```

- Additional configuration is required for your __Library__ modules, if you
  plan to use `@KoinEffect` annotation within library code.

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

- Explore an example Koin Multi-module project [here](/app-examples/koin/app-multimodule).

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

2. Inject the interface to a view-model constructor:

   ```kotlin
   class CatsViewModel(
       // inject your interface here:
       val myEffects: MyEffects
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

3. Implement the interface and annotate the implementation with `@KoinEffect`. 
   This will automatically generate a Koin module for you. Additionally, you 
   can safely use an activity reference or any other UI-related components within 
   the implementation:

   ```kotlin
   @KoinEffect // <-- do not forget this annotation
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

4. Use the implementation either in @Composable functions or in Activity/Fragment classes :

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

5. Set up the Koin Application definition, for example, in your Application class:

   ```kotlin
   class MyApp : Application() {
       
       val viewModelsModule = module {
           viewModelOf(::CatsViewModel)
       }
   
       override fun onCreate() {
           super.onCreate()
           startKoin {
               // setup android dependencies if needed
               androidContext(this@App)
               androidLogger()
               // setup your own Koin modules,
               modules(viewModelsModule)

               // call this function at the end to register
               // all effects annotated with @KoinEffect:
               installAnnotatedKoinEffects()
           }
       }
   }
   ```

6. Don't forget to register the application class in `AndroidManifest.xml`:

   ```xml
   <application
       android:name=".MyApp" />
   ```

Check out [example apps](/app-examples/koin) for more details ;)

## Koin Scopes and Effects

By default, all effects are installed globally as singletons. Additionally, effects 
are cleared automatically when they are injected into ViewModels (either directly or 
through other dependencies declared via the `factory()` Koin function).

Optionally, you can limit the lifetime of any effect by manually scoping it. 
For instance, let's limit `MyEffects` to the Retained Activity Lifecycle:

1. Add either `@InstallEffectToClassScope` or `@InstallEffectToNamedScope` annotation 
   to the effect implementation class:

   ```kotlin
   @KoinEffect
   @InstallEffectToClassScope(MainActivity::class)
   class MyEffectsImpl : MyEffects
   ```
2. Add ViewModels that use scoped effect interfaces to the same scope or to another 
   scope linked to the effect scope:

   ```kotlin
   val viewModelsModule = module {
       scope<MainActivity> {
           viewModelOf(::CatsViewModel)
       }
   }
   ```

3. Create the `MainActivity` scope:

   ```kotlin
   class MainActivity : ComponentActivity(), AndroidScopeComponent {
       
       override val scope: Scope by activityRetainedScope()
   
       // projects without Jetpack Compose
       private val toasts by lazyEffect { MyEffectsImpl(context = this) }
   
       // projects with Jetpack Compose
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContent {
               KoinActivityScope {
                   val toasts = remember { MyEffectsImpl() }
                   EffectProvider(toasts) {
                       // ...
                   }
               }
           }
       }
   }
   ```

## Using Effect Controllers

__Effect Controllers__ are low-level alternatives to the `lazyEffect` delegate and the `EffectProvider` composable function. They let you manually connect an effect implementation to its corresponding interface, offering more control when dealing with custom components or non-standard lifecycles.

In Koin, you can obtain an `EffectController` from `KoinComponent`, `KoinScope`, `Koin`, or `AndroidScopeComponent` using the following extension functions:
- `getEffectController()` - instantly creates and returns an `EffectController` instance.
- `injectEffectController()` - provides a lazy delegate that initializes the controller on first access.

For instance, you might prefer starting effects in `onResume` instead of the default `onStart`:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity(), KoinComponent {

    private val controller: EffectController<MyEffectsImpl> by injectEffectController()

    override fun onResume() {
        super.onResume()
        controller.start(MyEffectsImpl(this))
    }

    override fun onPause() {
        super.onPause()
        controller.stop()
    }
}
```

__Bound Effect Controller__ offers a more convenient approach. It initializes a predefined effect implementation on first access, caches it, and automatically links it to the corresponding interface when you call `BoundEffectController.start()` method. 

You can create a `BoundEffectController` in much the same way as an `EffectController`, using extension functions available on `KoinComponent`, `KoinScope`, `Koin`, or `AndroidScopeComponent`:
- `getBoundEffectController { ... }` - immediately creates and returns a `BoundEffectController` instance.
- `injectBoundEffectController { ... }` - returns a lazy delegate that initializes the bound controller upon first access.

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity(), KoinComponent {

    private val boundController by injectBoundEffectController {
        MyEffectsImpl()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // you can access MyEffectsImpl at any time, e.g. before onResume:
        val myEffectsImpl = boundController.effectImplementation 
    }

    override fun onResume() {
        super.onResume()
        boundController.start()
    }

    override fun onPause() {
        super.onPause()
        boundController.stop()
    }
}
```

## Example Apps

Check out [example apps](/app-examples/koin) for more details ;)

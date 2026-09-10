# Effects + Hilt guide

This page describes how to install and use the library with Hilt DI Framework.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation (Single-module Projects)](#installation-single-module-projects)
- [Installation for Multi-Module Projects](#installation-for-multi-module-projects)
- [Migration from Version 1.x to 2.x](#migration-from-version-1x-to-2x)
- [Usage Example](#usage-example)
- [Creating Effect Implementations with Hilt](#creating-effect-implementations-with-hilt)
- [Hilt Components and Effects](#hilt-components-and-effects)
- [Using Effect Controllers](#using-effect-controllers)
- [Example Apps](#example-apps)

## Prerequisites

1. Use the latest version of Android Studio.
2. Use __Kotlin v2.0__ or above.
3. Ensure the [KSP](https://kotlinlang.org/docs/ksp-overview.html) plugin is added to your project. [How to install KSP?](/docs/ksp-installation.md).
4. Make sure [Hilt] is properly set up in your project. [How to install Hilt?](/docs/ksp-and-hilt-installation.md)

## Installation (Single-module Projects)

1. Add [Hilt and KSP](/docs/ksp-and-hilt-installation.md) to your Android project.
2. Add the following dependencies:

   ```kotlin
   // annotation processor (required):
   ksp("com.uandcode:effects2-hilt-compiler:2.2.0")
   // for projects without Jetpack Compose:
   implementation("com.uandcode:effects2-hilt:2.2.0")
   // for projects with Jetpack Compose:
   implementation("com.uandcode:effects2-hilt-compose:2.2.0")
   ```

For more details, check out the [single-module Hilt example app](/app-examples/hilt/app-singlemodule).

## Installation for Multi-Module Projects

- Dependencies for your __application module__ remain the same:

  ```kotlin
  ksp("com.uandcode:effects2-hilt-compiler:2.2.0")
  implementation("com.uandcode:effects2-hilt:2.2.0") // without Jetpack Compose
  implementation("com.uandcode:effects2-hilt-compose:2.2.0") // with Jetpack Compose
  ```

- Additional configuration is required for your __Library__ modules, if you
  plan to use `@HiltEffect` annotation within library code.

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

- Explore an example Hilt Multi-module project [here](/app-examples/hilt/app-multimodule).

## Migration from Version 1.x to 2.x

With the major 2.x release, there have been changes to the groupId, packages, 
and imports. Please make sure to update your project accordingly.

1. In your `build.gradle` file, update the library dependencies:

   ```kotlin
   ksp("com.uandcode:effects2-hilt-compiler:2.2.0")
   implementation("com.uandcode:effects2-hilt:2.2.0") // without Jetpack Compose
   implementation("com.uandcode:effects2-hilt-compose:2.2.0") // with Jetpack Compose
   ```

2. Update relevant import statements and package references:

   ```kotlin
   // @HiltEffect annotation:
   import com.uandcode.effects.hilt.annotations.HiltEffect
   
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

2. Inject the interface to a view-model constructor (yep, you don't need to create a separate Hilt module):

   ```kotlin
   @HiltViewModel
   class CatsViewModel @Inject constructor(
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

3. Implement the interface and annotate the implementation with `@HiltEffect`.
   As a result, a Hilt module will be automatically generated for you. And
   in addition, you can safely use an activity reference or any other UI-related
   stuff in the implementation:

   ```kotlin
   @HiltEffect // <-- do not forget this annotation
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
     @AndroidEntryPoint
     class MainActivity: AppCompatActivity() {

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
     @AndroidEntryPoint
     class MainActivity : AppCompatActivity() {

         // option 1 (if you need an access to MyEffectsImpl instance):
         private val myEffects by lazyEffect {
             MyEffectsImpl(this)
         }
     
         override fun onCreate(savedInstanceState: Bundle?) {
             super.onCreate(savedInstanceState)
             // option 2 (if you don't need an access to MyEffectsImpl instance):
             initEffect { MyEffectsImpl(this) }
         }
     }
     ```

## Creating Effect Implementations with Hilt

In all examples above, effect implementations are created manually, e.g.
`MyEffectsImpl(this)`. This is not a requirement. The library never creates
effect implementations by itself, it only accepts either a ready instance or a
factory function:
- `lazyEffect { ... }` and `initEffect { ... }` accept a `() -> T` factory
- `EffectProvider(...)` and `EffectController.start(...)` accept a ready instance

Also, the module generated by the `@HiltEffect` annotation provides only an
`EffectController<MyEffectsImpl>` instance, but not the `MyEffectsImpl` class
itself. As a result, you can let Hilt create your effect implementations, and
there will be no duplicate bindings.

### Effect Implementations with @Inject Constructor

If all constructor arguments of your effect implementation can be provided by
Hilt, simply add an `@Inject` constructor:

1. Add an `@Inject` constructor to the effect implementation:

   ```kotlin
   @HiltEffect
   class MyEffectsImpl @Inject constructor(
       @ActivityContext private val context: Context,
   ) : MyEffects {
       // ...
   }
   ```

2. Inject the implementation into your activity and pass it to the `lazyEffect`
   delegate or to the `initEffect` function:

   ```kotlin
   @AndroidEntryPoint
   class MainActivity : AppCompatActivity() {

       @Inject
       lateinit var myEffectsProvider: Provider<MyEffectsImpl>

       // option 1 (if you need an access to MyEffectsImpl instance):
       private val myEffects by lazyEffect { myEffectsProvider.get() }

       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           // option 2 (if you don't need an access to MyEffectsImpl instance):
           initEffect { myEffectsProvider.get() }
       }
   }
   ```

   `Provider<MyEffectsImpl>` (or `dagger.Lazy<MyEffectsImpl>`) keeps the
   implementation lazy, so it is created only when the effect is actually
   attached. You can also inject the implementation directly
   (`@Inject lateinit var myEffectsImpl: MyEffectsImpl`), because Hilt injects
   fields within a `super.onCreate()` call, and the factory function is executed
   later.

3. Optionally, you can use an injected implementation with an `EffectProvider`
   composable function:

   ```kotlin
   @AndroidEntryPoint
   class MainActivity : AppCompatActivity() {

       @Inject
       lateinit var myEffectsImpl: MyEffectsImpl

       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContent {
               EffectProvider(myEffectsImpl) {
                   MyApp()
               }
           }
       }
   }
   ```

### Effect Implementations with Assisted Injection

Some arguments can't be provided by Hilt. For example, a `NavHostController` is
created by a `@Composable` function, so it is not a part of the Hilt dependency
graph. In this case, you can mix both types of arguments by using an assisted
injection:

1. Annotate the constructor with `@AssistedInject`, mark manually provided
   arguments with `@Assisted`, and add an `@AssistedFactory` interface:

   ```kotlin
   @HiltEffect
   class MyEffectsImpl @AssistedInject constructor(
       // this argument is provided manually:
       @Assisted private val navController: NavHostController,
       // and this one is provided by Hilt:
       @ApplicationContext private val context: Context,
   ) : MyEffects {

       @AssistedFactory
       interface Factory {
           fun create(navController: NavHostController): MyEffectsImpl
       }

       // ...
   }
   ```

   :warning: Note: the class annotated with `@HiltEffect` should remain a
   top-level class, but the `@AssistedFactory` interface can be nested.

2. Inject the factory instead of the implementation, and create the
   implementation when the missing arguments become available:

   ```kotlin
   @AndroidEntryPoint
   class MainActivity : AppCompatActivity() {

       @Inject
       lateinit var myEffectsFactory: MyEffectsImpl.Factory

       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContent {
               val navController = rememberNavController()
               val myEffectsImpl = remember {
                   myEffectsFactory.create(navController)
               }
               EffectProvider(myEffectsImpl) {
                   MyApp()
               }
           }
       }
   }
   ```

   The same factory can be used with the `lazyEffect` delegate and with the
   `initEffect` function, as long as all assisted arguments are available there.
   For example, an activity instance can be passed to the implementation
   explicitly:

   ```kotlin
   @HiltEffect
   class MyEffectsImpl @AssistedInject constructor(
       @Assisted private val activity: ComponentActivity,
       @ApplicationContext private val context: Context,
   ) : MyEffects {

       @AssistedFactory
       interface Factory {
           fun create(activity: ComponentActivity): MyEffectsImpl
       }

       // ...
   }

   @AndroidEntryPoint
   class MainActivity : AppCompatActivity() {

       @Inject
       lateinit var myEffectsFactory: MyEffectsImpl.Factory

       private val myEffects by lazyEffect { myEffectsFactory.create(this) }
   }
   ```

### Things to Keep in Mind

- Fields injected into an activity are provided by an `ActivityComponent`. This
  means that `Activity` and `@ActivityContext Context` arguments are available
  for your effect implementations. At the same time, the implementation itself
  can't be injected into a ViewModel, and this is an expected behavior: only
  effect interfaces are intended to be injected into components with a longer
  lifecycle.
- Do not annotate effect implementations with `@Singleton` or
  `@ActivityRetainedScoped`. Such implementations may hold references to UI
  components, so these scopes cause memory leaks - the exact problem this
  library is intended to solve. Leave the implementation unscoped or use
  `@ActivityScoped` at most.
- An unscoped implementation is created again for each injection point. This is
  usually fine, because the `lazyEffect` delegate and the `initEffect` function
  call the factory function only once. But if you attach more than one instance
  to the same interface, they are all treated as separate effect handlers.
  Please check out the [Multiple Effect Handlers](/docs/advanced-concepts.md#multiple-effect-handlers)
  section for more details.

## Hilt Components and Effects

By default, all effect interfaces are installed into a Hilt `ActivityRetainedComponent`. 
This allows you to inject effect interfaces directly into a ViewModel constructor. 
Additionally, all effects injected into ViewModels are automatically cleared, 
even if they have a longer lifecycle.

You can change the Hilt Component where an effect is installed by using the `@HiltEffect` annotation 
with the `installIn` parameter:

```kotlin
@HiltEffect(
    // supported components: ActivityRetainedComponent (default),
    // SingletonComponent, ActivityComponent, FragmentComponent
    installIn = ActivityComponent::class,
)
class MyEffectsImpl : MyEffects
```

## Using Effect Controllers

__Effect Controllers__ are low-level alternatives to the `lazyEffect` delegate and the `EffectProvider` composable function. They let you manually connect an effect implementation to its corresponding interface, offering more control when dealing with custom components or non-standard lifecycles.

For instance, you might prefer starting effects in `onResume` instead of the default `onStart`:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var controller: EffectController<MyEffectsImpl>

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

__Bound Effect Controller__ offers a more convenient approach. It initializes a predefined effect implementation on first access, caches it, and automatically links it to the corresponding interface when you call `BoundEffectController.start()` method. You can create a `BoundEffectController` from any existing `EffectController` that hasn't yet been started by using `EffectController.bind()` extension function:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var controller: EffectController<MyEffectsImpl>

    private val boundController: BoundEffectController<MyEffectsImpl> by lazy {
        controller.bind { MyEffectsImpl(this) }
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

Check out [example apps](/app-examples/hilt) for more details ;)

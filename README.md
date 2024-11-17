# Hilt plugin for easier implementation of one-time events (a.k.a. side effects) :fire:

Now compatible with KSP and Jetpack Compose starting from version `0.0.4`.

[![Maven Central](https://img.shields.io/maven-central/v/com.elveum/effects-core.svg?label=Maven%20Central)](https://elveum.com/sh/effects)
[![License: Apache 2](https://img.shields.io/github/license/romychab/effects-hilt-plugin)](LICENSE)
![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)
![JDK](https://img.shields.io/badge/JDK-17-brightgreen.svg?style=flat)
![Android Studio](https://img.shields.io/badge/Android%20Studio-Ladybug-brightgreen.svg?style=flat)

This plugin simplifies working with one-time events. It allows injecting side-effect interfaces
to your `ViewModel`. For example, you can show toasts, display dialogs, do navigation stuff in view-model class
without memory leaks.

## Installation

1. Use the latest version of Android Studio (with embedded JDK 17)
2. Make sure [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is added properly to your project.
   Minimal version of Hilt should be `2.48.1`.
3. Make sure `ksp` plugin is added to the `build.gradle`:

   ```
   plugins {
       ...
       id 'com.google.devtools.ksp'
       ...
   }
   ```

4. Add the following dependencies:

```
// annotation processor (required):
ksp "com.elveum:effects-processor:0.0.4"
// for projects with Jetpack Compose:
implementation "com.elveum:effects-compose:0.0.4"
// for projects without Jetpack Compose:
implementation "com.elveum:effects-core:0.0.4"
```

## How to use

The main idea of this plugin is to simplify one-time events by extracting them to a separate
interface that can be injected to the view-model constructor.

Let's imagine you want to:
- trigger navigation logic from view-model
- show an alert dialog and get the user choice in view-model
- show toasts, snackbars, etc.

1. Define one or more interfaces of side-effects:

   ```kotlin
   interface UiEffects {
       // simple effect (one-time event)
       fun launchCatDetails(cat: Cat)

       // effect which can return a result data
       suspend fun showAskDialog(message: String): Boolean

       // effect which can emit infinite number of results
       fun listenUserInput(): Flow<String>
   }
   ```

2. Inject the interface to your view-model and implement the view-model's logic:

   ```kotlin
   @HiltViewModel
   class CatsListViewModel @Inject constructor(
       // inject your interface here:
       val uiEffects: UiEffects
   ): ViewModel() {

       fun onCatChosen(cat: Cat) = viewModelScope.launch {
           val confirmed = uiEffects.showAskDialog(
               message = "Are you sure you want to open details screen?"
           )
           if (confirmed) {
               uiActions.launchCatDetails(cat)
           }
       }

   }
   ```

3. Implement the interface. All you need is to annotate the implementation with `@SideEffect`.
   Also you can optionally add dependencies from the Hilt graph to the
   constructor (supported components: `@SingletonComponent`, `@ActivityRetainedComponent`,
   `@ActivityComponent`):

   ```kotlin
   @SideEffect
   class UiEffectsImpl(
       // optional arg #1 - activity itself
       private val activity: FragmentActivity,
       // optional arg #2 - container for retained data which
       // can survive screen rotation
       private val retainedData: RetainedData,
       // other args from Hilt graph
       private val somethingElse: SomethingElse,
   ) : UiEffects {

       override fun launchCatDetails(cat: Cat) {
           // use activity reference for doing navigation (via fragment manager,
           // navigation component, etc.)
       }

       override suspend fun showAskDialog(message: String): Boolean {
           // suspend function is automatically cancelled when activity is stopped
           // and then executed again when activity is started
           return suspendCancellableCoroutine { continuation ->
               //
               // show alert dialog here
               //
               continuation.invokeOnCancellation {
                   // cancel alert dialog here, e.g.:
                   dialog.dismiss()
               }
           }
       }

       override fun listenUserInput(): Flow<String> {
           // flow is automatically cancelled when activity is stopped
           // and then executed again when activity is started
           return callbackFlow {
             // do something here
           }
       }
   }
   ```

4. Enable side-effects for the activity. If you use a single-activity
   approach then you need to do this only once in the main activity.

   ```kotlin
   @AndroidEntryPoint
   class MainActivity: AppCompatActivity() {

       // only for old non-Jetpack Compose projects:
       @Inject
       lateinit var attachSideEffects: AttachSideEffects

       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate()
           setContent {
               // only for Jetpack Compose projects:
               EffectsApp {
                   // call your main composable function here
               }
           }
       }
   }
   ```

Any side-effect implementation can be accessed from the `@Composable` functions
by using `getEffect<T>()` call:

```kotlin
interface Router {
  fun navigate(route: String)
}

@SideEffect
class RouterImpl : Router {
  private var navController: NavController? = null

  override fun navigate(route: String) {
    navController.navigate(route)
  }

  fun setNavController(navController: NavController) {
    this.navController = navController
  }
}

@Composable
fun MyApp() {
  val navController = rememberNavController()
  // use getEffect() for retrieving side-effect implementation class:
  val routerImpl = getEffect<RouterImpl>()
  SideEffect {
    // initialize router
    routerImpl.setNavController(navController)
  }

  NavHost(
    navController = navController,
    startDestination = "main",
  ) {
    // ...
  }
}
```

Also you can check an example app in the `:app` module.

That's it!

## Prerequisites

This plugin is intended for kotlin projects which already use [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) dependency injection framework.

## Limitations

- Non-suspend methods can't return values. This is due to the different lifecycle of view-models
  and activity/fragments. You can't get a value from the activity when view-model is active
  but activity isn't.

  Example of valid case:

  ```kotlin
  interface Dialogs {
      suspend fun showDialog(message: String): DialogResponse
  }
  ```

  Example of invalid case:

  ```kotlin
  interface Dialogs {
      fun showDialog(message: String): DialogResponse
  }
  ```

- Generic types are supported only for interface methods but not for the entire interface type.
  For example, you can write:

  ```kotlin
  interface Router {
      fun <T : Screen> launch(screen: T)
  }
  ```

  But the following definition is prohibited:

  ```kotlin
  interface Router<T : Screen> {
      fun launch(screen: T)
  }
  ```

- Side-effects are tied to the Activity lifecycle, not to the Fragment lifecycle. Be
  aware of this when using suspend functions / flows in your side-effect interfaces.

# Hilt plugin for easier implementation of one-time events (a.k.a. MVI side effects) :fire:

Now compatible with KSP and Jetpack Compose starting from version `0.0.7`.

[![Maven Central](https://img.shields.io/maven-central/v/com.elveum/effects-core.svg?label=Maven%20Central)](https://uandcode.com/sh/effects)
[![License: Apache 2](https://img.shields.io/github/license/romychab/effects-hilt-plugin)](LICENSE)
![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)
![JDK](https://img.shields.io/badge/JDK-17-brightgreen.svg?style=flat)
![Android Studio](https://img.shields.io/badge/Android%20Studio-Ladybug-brightgreen.svg?style=flat)

This plugin eliminates the usage of `Channel`, `SharedFlow`, or event properties in states.
As a result, you can work with MVI side effects (one-time events) more easily. For example,
you can show toasts, display dialogs, execute navigation commands in a view-model
without memory leaks. All you need to do is simply inject an interface to the view-model constructor.

## Installation

1. Use the latest version of Android Studio.
2. Make sure [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is added properly to your project.
   Minimal version of Hilt should be `2.48.1`.
3. Make sure [KSP](https://kotlinlang.org/docs/ksp-quickstart.html#add-a-processor) plugin is added to your project.
4. Add the following dependencies:

```
// annotation processor (required):
ksp "com.elveum:effects-processor:0.0.9"
// for projects with Jetpack Compose:
implementation "com.elveum:effects-compose:0.0.9"
// for projects without Jetpack Compose:
implementation "com.elveum:effects-core:0.0.9"
```

## How to use

The main idea of this plugin is to simplify one-time events by moving them to a separate
interface. No more SharedFlows, Channels, additional properties in states representing events, etc.

Let's imagine you want to:
- execute navigation commands
- show an alert dialog and get the user choice
- show toasts, snackbars, etc.

1. Define one or more interfaces of MVI-effects:

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

       fun onCatChosen(cat: Cat) {
           viewModelScope.launch {
               val confirmed = uiEffects.showAskDialog(
                   message = "Are you sure you want to open details screen?"
               )
               if (confirmed) {
                   uiEffects.launchCatDetails(cat)
               }
           }
       }

   }
   ```

3. Implement the interface. All you need is to annotate the implementation with `@MviEffect`.
   Also you can optionally add dependencies from the Hilt graph to the
   constructor (supported components: `@SingletonComponent`, `@ActivityRetainedComponent`,
   `@ActivityComponent`):

   ```kotlin
   @MviEffect // <-- do not forget this annotation
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

4. Enable MVI-effects for the activity. If you use a single-activity
   approach then you need to do this only once in the main activity.

   ```kotlin
   @AndroidEntryPoint
   class MainActivity: AppCompatActivity() {

       // only for old non-Jetpack Compose projects:
       @Inject
       lateinit var attachMviEffects: AttachMviEffects

       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate()
           setContent {
               // only for Jetpack Compose projects:
               MviEffectsApp {
                   // call your main composable function here
               }
           }
       }
   }
   ```

Any MVI-effect implementation can be accessed from the `@Composable` functions
by using `getMviEffect<T>()` call:

```kotlin
// effect interface:
interface Router {
  fun navigate(route: String)
}

// effect implementation:
@MviEffect
class RouterImpl : Router {
  private var navController: NavController? = null

  override fun navigate(route: String) {
    navController.navigate(route)
  }

  fun setNavController(navController: NavController) {
    this.navController = navController
  }
}

// composable function which initializes router:
@Composable
fun MyApp() {
  val navController = rememberNavController()
  // use getMviEffect() for retrieving implementation class:
  val routerImpl = getMviEffect<RouterImpl>()
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

// activity with top-level composition
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // wrap MyApp() into MviEffectsApp { ... } to allow
            // calling getMviEffect<T>() function:
            MviEffectsApp {
                MyApp()
            }
        }
    }
}
```

Also you can check an example app in the `:app` module.

That's it!

## Prerequisites

This plugin is designed for Android projects which already use [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) dependency injection framework.

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

- Generic types are supported only for methods but not for the entire interface type.
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

- MVI-effects are tied to the Activity lifecycle, not to the Fragment lifecycle. Be
  aware of this when using suspend functions / flows in your MVI-effect interfaces.

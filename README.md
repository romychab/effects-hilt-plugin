# Hilt plugin for easier implementation of side effects :fire:

[![Maven Central](https://img.shields.io/maven-central/v/com.elveum/effects-core.svg?label=Maven%20Central)](https://elveum.com/sh/effects)
[![License: Apache 2](https://img.shields.io/github/license/romychab/effects-hilt-plugin)](LICENSE)
![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)
![JDK](https://img.shields.io/badge/JDK-17-brightgreen.svg?style=flat)
![Android Studio](https://img.shields.io/badge/Android%20Studio-Giraffe-brightgreen.svg?style=flat)

This plugin allows injecting side-effect interfaces to your `ViewModel`.

For example, you can show toasts, display dialogs, do navigation stuff in view-model class:

```kotlin

// --- view-model

@HiltViewModel
class MyViewModel @Inject constructor(
    val router: Router,
) {

    fun onItemClicked(item: Item) {
        router.launchItemDetails(item)
    }
}

// --- router interface

interface Router {
  fun launchItemDetails(item: Item)
}

// --- router implementation

@SideEffect
class RouterImpl(
    // You can add any dependency from ActivityComponent, ActivityRetainedComponent
    // and SingletonComponent to this constructor.
    // And yep, it's safe to use activity here, there will not be memory leaks,
    // that's the magic of the plugin
    private val activity: FragmentActivity
) : Router {

    override fun launchItemDetails(item: Item) {
      // implement your navigation logic here
    }
}
```

Also you can check an example app in the `:app` module.

## Installation

1. Use the latest version of Android Studio (with embedded JDK 17)
2. Make sure [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is added properly to your project.
3. Make sure `kapt` plugin is added to the `build.gradle`:

   ```
   plugins {
       ...
       id 'kotlin-kapt'
       ...
   }
   ```

4. Add the following dependencies:

```
implementation "com.elveum:side-effects-core:0.0.1"
kapt "com.elveum:side-effects-processor:0.0.1"
```

## How to use

Let's imagine you want to:
- control navigation in view-model
- show an alert dialog and get the user choice in view-model

This plugins allow you safely interacting with activity from the view-model.

1. Define one or more interfaces of side-effects:

   ```kotlin
   interface UiActions {
       // simple effect (one-shot event)
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
       val uiActions: UiActions
   ): ViewModel() {

       fun onCatChosen(cat: Cat) = viewModelScope.launch {
           val confirmed = uiActions.showAskDialog(
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
   constructor (supported components: @SingletonComponent, @ActivityRetainedComponent,
   @ActivityComponent):

   ```kotlin
   @SideEffect
   class DefaultUiActions(
       // optional arg #1 - activity itself
       private val activity: FragmentActivity,
       // optional arg #2 - container for retained data which
       // can survive screen rotation
       private val retainedData: RetainedData,
       // other args from Hilt graph
       private val somethingElse: SomethingElse,
   ) : UiActions {

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
       @Inject
       lateinit var attachSideEffects: AttachSideEffects
   }
   ```

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

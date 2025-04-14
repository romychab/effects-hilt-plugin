# Key Concepts

This document outlines the core ideas behind the library and explains how it works.

## Table of Contents

- [What Is an Effect (in general)?](https://github.com/romychab/effects-hilt-plugin/blob/v2-docs/docs/key-concepts.md#what-is-an-effect-in-general)
- [What Is an Effect (in this library)?](https://github.com/romychab/effects-hilt-plugin/blob/v2-docs/docs/key-concepts.md#what-is-an-effect-in-this-library)
- [Glossary](https://github.com/romychab/effects-hilt-plugin/blob/v2-docs/docs/key-concepts.md#glossary)
- [Advanced Glossary](https://github.com/romychab/effects-hilt-plugin/blob/v2-docs/docs/key-concepts.md#advanced-glossary)
- [A closer look at the three effect types](https://github.com/romychab/effects-hilt-plugin/blob/v2-docs/docs/key-concepts.md#a-closer-look-at-the-three-effect-types)
  - [One-off Event](https://github.com/romychab/effects-hilt-plugin/blob/v2-docs/docs/key-concepts.md#one-one-off-event)
  - [Suspend Call](https://github.com/romychab/effects-hilt-plugin/blob/v2-docs/docs/key-concepts.md#two-suspend-call)
  - [Flow Call](https://github.com/romychab/effects-hilt-plugin/blob/v2-docs/docs/key-concepts.md#three-flow-call)

## What Is an Effect (in general)?

In the context of the MVI (Model–View–Intent) architecture, an __effect__ refers to actions that don't 
naturally fit into the main unidirectional data flow. A common example is one-off events, 
such as:
- Navigating to another screen
- Showing a toast, etc.

By default, ViewModels in Android don't provide a built-in mechanism for handling 
one-off events. Developers typically work around this by using `Channel`, `SharedFlow`, or by 
embedding event properties inside state classes.


## What Is an Effect (in this library)?

The library takes the concept of effects beyond just one-off events. 
Here, an effect is defined more broadly as:

> Any action that occurs outside the main data flow, initiated by a component with a 
> longer lifecycle and targeted at a component with a shorter lifecycle.

This includes one-off events, but also covers more advanced use cases such as lifecycle-bound listeners, 
and callbacks between layers of your app.

## Glossary

- __Effect__ - An action initiated by a component with a longer lifecycle that must be 
  handled by a component with a shorter lifecycle. There are three main types of effects:

  - One-off Event - A simple action where the result of processing is ignored. It follow a "fire-and-forget" principle.

    *Example: showing a toast message*
 
    ```kotlin
    interface MyEffects {
        // the simplest one-off event
        fun showToast(message: String)
    }
    ```
  
  - Suspend Call - A one-time action that returns a result to the caller, either a success or 
    an exception.

    *Example: showing an alert dialog and returning the user’s choice*

    ```kotlin
    interface MyDialogs {
        // show an AlertDialog, so user can make a choice
        suspend fun askSystemDirRemoval(): Boolean
    }
    ```

  - Flow Call - A subscription that emits multiple results over time.

    *Example: subscribing to user interaction events*

    ```kotlin
    interface MyUiActions {
        fun listenClicks(): Flow<OnClickEvent>
    }
    ```
    
- __Effect Interface__ (also known as __Target Interface__, or __Effect Sender__) - An interface that defines 
  a set of effects. It is injected into ViewModels (or other long-lived components), 
  and is used to initiate effects. This approach offers two key advantages:
  - Abstraction from implementation details – for instance, your ViewModels don’t need 
    to know anything about Activity or Context.
  - Avoidance of duplication - The same effect interface can be reused across multiple 
    screens or components (e.g., for showing toast messages in two different screens).

- __Effect Implementation__ (or __Effect Handler__) - A class that implements the __Effect Interface__. The library's 
  core feature is the ability to inject Effect Interfaces into long-lived components, while 
  the Effect Implementations can safely reference Activity or other short-lived components. 
  This is the core magic of the library.

- `lazyEffect` delegate and `EffectProvider` Composable function - High-level connectors that activate 
  effect processing when the associated Activity becomes active. They ensure that emitted 
  effects are safely delivered to the appropriate implementation.

## Advanced Glossary

For a deeper dive into how the library works, see the [Advanced Concepts](/docs/advanced-concepts.md) page.

## A closer look at the three effect types

As mentioned earlier, the library supports three distinct types of effects:
- One-off Event - A simple fire-and-forget action.
- Suspend Call - A one-time action that returns a result (or throws an exception).
- Flow Call - A continuous stream of results, potentially infinite.

The examples below use the `@HiltEffect` annotation. However, the same patterns apply 
to other DI Frameworks:
- Use `@KoinEffect` for Koin-based projects.
- Use `@EffectClass` if you're not using any DI framework.

Refer to the dedicated documentation for your preferred DI setup:
- [Effects + Hilt](/docs/effects-and-hilt.md)
- [Effects + Koin](/docs/effects-and-koin.md)
- [Pure Effects (No DI)](/docs/effects-pure.md)

### :one: One-off Event

It is just one-off event. One-off events can be used when you don't
need the result of the execution. A simple example is displaying a Toast message.

In order to declare a one-off event, simply write a regular __non-suspend__ method that
__doesn’t return anything__:

```kotlin
interface Toasts {
    fun showToast(@StringRes messageId: Int)
}
```

Handling such an event is not a complex task:

```kotlin
@HiltEffect
class ToastsImpl(
    private val context: Context,
) : Toasts {
    override fun showToast(@StringRes messageId: Int) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
    }
}
```

Usage example:

```kotlin
@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    private val toasts by lazyEffect { ToastsImpl(this) }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val toasts: Toasts,
) : ViewModel() {

    fun doSomething() {
        // ... 

        // show a toast message 1 time when activity is active
        // (between onStart / onStop)
        toasts.showToast(R.string.error_message)
    }

}
```

Such one-off events are handled immediately if the Activity is at least in
a STARTED state (after `onStart()` is called and before `onStop()`).
Otherwise, the event is queued until the Activity becomes STARTED.

:warning: It’s worth mentioning exceptions separately. If the method throws an exception,
it will be ignored (the app won’t crash). However, the exception will be logged
in LogCat. You can consider that the error is also a result of handling a one-off
event. And as mentioned above, any result of handling such an event is ignored
and not delivered back to the ViewModel.

If you still need not only to send a one-off event but also to receive the result
of its execution (whether success or error, it doesn’t matter), then consider
the second type of calls - Suspend Calls.

### :two: Suspend Call

This is more advanced effect because it can return a result - either success or an
error (`Exception`). You can think of this type of effect as a request with an
expected response, or as a one-off event whose handling result can be delivered back to the caller.

You can declare such a call by using suspend functions:

```kotlin
interface Dialogs {
    suspend fun ask(@StringRes messageId: Int): Boolean
}
```

Let’s consider an implementation example:

```kotlin
@HiltEffect
class DialogsImpl : Dialogs {
    override suspend fun ask(@StringRes messageId: Int): Boolean {
       return suspendCancellableCoroutine { continuation ->
          val alertDialog = AlertDialog.Builder(context)
             .setTitle("Dialog Title")
             .setMessage(messageId)
             .setPositiveButton("Yes") { _, _ -> continuation.resume(true) }
             .setNegativeButton("No") { _, _ -> continuation.resume(false) }
             .create()
          alertDialog.show()
          continuation.invokeOnCancellation {
             alertDialog.dismiss()
          }
       }
    }
}
```

Also you can show a dialog with Jetpack Compose. [Here is](/app-examples/hilt/app-singlemodule/src/main/java/com/uandcode/example/hilt/singlemodule/effects/dialogs/ComposeDialogs.kt) 
an example of such implementation.

Now let's take a look at the usage example:

```kotlin
// Activity:
@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dialogs = remember { DialogsImpl() }
            EffectProvider(dialogs) {
                MyApp()
            }
        }
    }
    
}

// ViewModel
@HiltViewModel
class MainViewModel @Inject constructor(
    private val dialogs: Dialogs,
) : ViewModel() {

    fun doSomething() {
        viewModelScope.launch {
            try {
                val confirmed = dialogs.ask(R.string.question)
                if (confirmed) {
                    // do something
                }
            } catch (e: Exception) {
                // exceptions can be easily caught
            }
        }
    }

}
```

As you see, you can receive both a user choice and an exception in your view-model.
Also, like the previous type of calls, here `dialogs.ask(...)` is executed immediately
if the Activity is in a STARTED state. Otherwise, the execution will be queued until
`Activity.onStart` is called.

The execution itself looks different from the ViewModel's perspective and from
the effect implementation's perspective. From the ViewModel's perspective,
the execution is not cancelled when the Activity is stopped, the `dialogs.ask(...)` call just
waits for the restart of the Activity. But from the effect implementation's perspective,
the call is cancelled when the Activity is stopped, and then re-executed again when
the Activity is restarted.

### :three: Flow Call

The third type of call allows you to get multiple or even an infinite number of
results. For example, you can implement event listening behavior.

To declare this type of call, you need to add a __non-suspend__ function that returns
`Flow<T>`:

```kotlin
interface ClickId

interface UiClicks {
    fun <T : ClickId> listenClicks(clazz: KClass<T>): Flow<T>
}

// optional inline function to avoid a direct usage of KClass:
inline fun <reified T : ClickId> UiClicks.listenClicks(): Flow<T> {
    return listenClicks(T::class)
}

```

Now, you can implement the interface by using `SharedFlow<ClickId>`:

```kotlin
@HiltEffect
class UiClicksImpl : UiClicks {

    private val clickFlow = MutableSharedFlow<ClickId>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override fun <T : ClickId> listenClicks(clazz: KClass<T>): Flow<T> {
        return clickFlow.filterIsInstance(clazz)
    }

    fun sendClick(clickId: ClickId) {
        clickFlow.trySend(clickId)
    }

}
```

After that, you can use the `UiClicksImpl` class in `@Composable` functions or
Activity, sending button clicks in the following way:

`uiClicksImpl.sendClick(SignInClickId())`

And the `UiClicks` interface can be used for event listening:

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    uiClicks: UiClicks,
) : ViewModel() {

    init {
        viewModelScope.launch {
            uiClicks.collect { clickId ->
                // handle clicks here
            }
        }
    }

}
```

Kotlin Flow is a very powerful tool. But there are also some nuances to keep in
mind when using it. First, effect implementations can return both infinite and
finite Flows. In case of finite Flows, terminal operators such as `collect()` will
finish their work as soon as the Flow in the effect implementation completes.
Second, if an exception is thrown on the effect implementation side, the `collect()`
method will also throw an exception. Third, from the ViewModel's perspective,
terminal operators like `collect()` will not stop working when the Activity goes to the
STOPPED state. They will wait until the Activity goes back to the STARTED state.
At the same time, from the effect implementation’s perspective, the Flow will be
automatically cancelled after `onStop()` is called, and then restarted again after `onStart()`.

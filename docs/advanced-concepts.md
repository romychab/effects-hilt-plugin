# Advanced Details

The concepts described in the [Key Concepts](/docs/key-concepts.md) page are sufficient 
to understand and use the library’s main features. However, if you'd like to explore 
the internals and advanced mechanisms, this page dives deeper into how the library 
works under the hood.

## Table of Contents

- [Advanced Glossary](#advanced-glossary)
- [Multiple Effect Handlers](#multiple-effect-handlers)
- [Multiple Target Interfaces](#multiple-target-interfaces)
- [Manual Clean-Up](#manual-clean-up)
- [Default Lifecycle](#default-lifecycle)
- [Changing the Default Lifecycle](#changing-the-default-lifecycle)
- [Limitations](#limitations)

## Advanced Glossary

- __Effect Controller__ - A low-level alternative to the `lazyEffect` delegate or `EffectProvider`
  composable. Effect controllers allow you to manually connect an effect implementation to an 
  effect interface. They're particularly useful when working with custom components 
  or lifecycles.
- __Bound Effect Controller__ - A more user-friendly version of `EffectController`. 
  It initializes a predefined effect implementation on first access, caches it, 
  and automatically connects it to the effect interface.
- __Proxy Implementation__ / __Proxy__ - An auto-generated implementation of an effect 
  interface. This proxy is what actually gets injected into ViewModels instead of 
  your own effect implementation. You don't interact with proxies directly, but 
  understanding their role helps clarify how the library operates.
- __Effect Queue__ - An internal queue that holds effects until an appropriate 
  implementation is connected. When a ViewModel initiates an effect (e.g., showing 
  a toast), it gets enqueued via the proxy. If a matching implementation is connected, 
  the effect is delivered immediately; otherwise, it waits in the queue.
- __Effect Scope__ - A component that holds a scoped effect queue for each effect 
  interface. It's central to managing the lifecycle of effects. If you're not using 
  a DI framework, you can interact with scopes directly to handle proxies and effect 
  controllers.
- __Root Effect Scopes__ - The initial scopes provided by the `RootEffectScopes` object:
  - `RootEffectScopes.global`: Manages all annotated effects as singletons by default. 
    You can override it using `RootEffectScopes.setGlobal()` call.
  - `RootEffectScopes.empty`: A blank scope that allows you to create custom scopes 
    using `RootEffectScopes.empty.createChild()`.

## Multiple Effect Handlers

Up until now, we assumed that interfaces + implementations have a one-to-one relationship.
That is, we have a ViewModel that calls a method on the interface, and this call is
delegated to a single implementation on the Activity side. However, nothing prevents
you from connecting multiple instances to the same interface:

```kotlin
class MyActivity : AppCompatActivity() {
    val myEffectsImpl1 by lazyEffect { MyEffectsImpl(this) }
    val myEffectsImpl2 by lazyEffect { MyEffectsImpl(this) }
}

@HiltViewModel // or @KoinEffect, @EffectClass, etc.
class MyViewModel @Inject constructor(
    private val myEffects: MyEffects
) : ViewModel() {
    // ...
}
```

In this case, when multiple implementations are connected to the same interface,
calls on the interface can be delegated differently to the corresponding
implementations:

1. Regular one-off events (non-suspend methods that don’t return a result) are
   handled only once. That is, they are sent to only the last connected active instance.
2. Suspend Calls (suspend methods in the interface) are delegated to the last connected
   active instance. But if the instance is disconnected during the execution, the suspend
   method is cancelled and then restarted on the next active connected instance.
3. Flow Calls (non-suspend methods that return Flow<T>) trigger the corresponding
   methods on all active connected instances in parallel and combine all the data into
   a resulting Flow returned by the interface. If any of the instances throws an Exception,
   a terminal operator (`collect()`) on a Flow returned by the interface will
   rethrow that exception. If all Flows are finite and complete their
   work, the resulting Flow will also complete.

## Multiple Target Interfaces

Starting from version 1.0.3, you can implement as many target interfaces as you like
in a single class:

```kotlin
// Both CatListRouter and CatDetailsRouter can be injected into a ViewModel (proxy implementation will be
// generated for both of them)
@HiltEffect // or @KoinEffect, @EffectClass, etc.
class CombinedRouter : CatListRouter, CatDetailsRouter {
    // ...
}
```

Additionally, you can explicitly specify the list of target interfaces in the annotation:

```kotlin
// Since the Runnable interface is not included in the 'targets' argument, it will be ignored:
@HiltEffect(
    targets = [CatListRouter::class, CatDetailsRouter::class]
)
class CombinedRouter : CatListRouter, CatDetailsRouter, Runnable {
    // ...
}
```

## Manual Clean-Up

All Suspend-, and Flow- calls are automatically released when you cancel a `CoroutineScope` which
has been used for the execution of that calls (for example, when `viewModelScope` is cancelled upon
destroying of ViewModel). In addition, one-off events are released when a ViewModel
is going to be destroyed, if you use extensions for Koin or Hilt. But sometimes you should manually 
cancel one-off events. For this purpose, you can implement `AutoCloseable` interface:

```kotlin
interface MyEffects : AutoCloseable {
  // add default implementation that does nothing;
  // a generated proxy will override and 
  // implement this method automatically
  override fun close() = Unit
}
```

## Default Lifecycle

By default, all effect interfaces are installed to:
- Hilt `ActivityRetainedComponent` (if you are using Hilt DI framework)
- Root scope of Koin Application (if you are using Koin)
- `RootEffectScopes.global` (if you are not using DI frameworks)

This allows you injecting effect interfaces directly to a view-model constructor.

You can treat any effect interface injected to a view-model constructor as an __effect sender__,
and any effect implementation created by `lazyEffect` delegate or by `EffectProvider` @Composable
function as an __effect handler__.

If there is at least one active handler, it can process incoming effects sent by the interface.
So actually, when you use `lazyEffect` or `EffectProvider`, you connect an effect handler to an effect sender.
If there is no active effect handlers, effects are added to a queue.

![Scheme image here](/docs/pic1.png)

Let's take a brief look at different ways of connecting effect handlers:

1. `lazyEffect { ... }` delegate can be used directly in activities and/or fragments.
   It automatically connects an effect handler when an activity/fragment
   is started, and then disconnects it when the activity/fragment is stopped.
2. `EffectProvider { ... }` composable function is intended to be used in
   other @Composable functions. It works almost in the same way
   as `lazyEffect` delegate, but also it automatically disconnects the effect implementation
   when `EffectProvider` composition is going to be destroyed. Also `EffectProvider` provides
   you an additional function `getEffect<T>()` which can be used for retrieving handlers.
3. `EffectController<T>` instances; usually you don't need to use effect controllers
   directly. But in rare cases it may be useful, e.g. in custom UI components. Effect
   controllers are not created by hands; instead they are automatically provided by DI framework.

   ```kotlin
   // Hilt:  
   @Inject
   lateinit var effectController: EffectController<MyEffectImpl>
   // Koin:
   val effectController by injectEffectController<MyEffectsImpl>()
   // without DI frameworks:
   val effectController = RootEffectScopes.global.getController<MyEffectsImpl>()
   
   // attach an effect handler to the interface
   effectController.start(MyEffectImpl())
   
   // detach the previously attached effect handler
   effectController.stop()
   ```
   
   Any `EffectController` instance can be converted to a `BoundEffectController`
   by using `EffectController.bind { ... }` extension function.

## Changing the Default Lifecycle

As previously mentioned, by default, all effect interfaces are installed into the 
following lifecycles depending on your setup:
- Hilt: `ActivityRetainedComponent`
- Koin: Root scope of the Koin Application
- No DI framework: `RootEffectScopes.global`

This default setup ensures that effect interfaces can be safely injected into
ViewModels. 

Additionally, when using Hilt or Koin, all effects injected into ViewModels are 
automatically cleared, even if they were installed as singletons.

You can override this default behavior to better suit your architecture or lifecycle needs. 
For example:

1. Hilt:

   `@HiltEffect` annotation has an additional `installIn` parameter, which can 
   change Hilt Component where an effect is installed in:

   ```kotlin
   @HiltEffect(
       // supported components: ActivityRetainedComponent (default),
       // SingletonComponent, ActivityComponent, FragmentComponent
       installIn = ActivityComponent::class,
   )
   class MyEffectsImpl : MyEffects
   ```
   
2. Koin:

   Koin does not have pre-defined Android scopes, so you must create them manually
   and use either `@InstallEffectToClassScope` or `@InstallEffectToNamedScope` annotation.
   Check out an example at the end of [this page](/docs/effects-and-koin.md).

   ```kotlin
   @KoinEffect
   @InstallEffectToClassScope(MainActivity::class)
   class MyEffectsImpl : MyEffects
   ```
   
3. Without DI frameworks:

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

## Limitations

- Non-suspend methods can't return values. This is due to the different lifecycle of view-models
  and activities. It is impossible to get a value from the activity when the view-model is active
  but the activity isn't.

  Example of valid code:

  ```kotlin
  interface Dialogs {
      suspend fun showDialog(message: String): DialogResponse
  }
  ```

  Example of invalid code:

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

- Multiple effect implementations can point to the same target interface, and vice versa. But 
  in this case they should be installed into the same scope (Hilt component).

  For example, this is a target interface:

  ```kotlin
  interface Router {
      fun launch(route: String)
  }
  ```

  You can create two or more implementations like this:

  ```kotlin
  @HiltEffect
  class Router1 : Router { ... }
  
  @HiltEffect
  class Router2 : Router { ... }
  ```

  Also, you can specify additional parameters, but they should be the same
  for both implementations:

  ```kotlin
  @HiltEffect(
    installIn = SingletonComponent::class,
  )
  class Router1 : Router { ... }
  
  @HiltEffect(
    installIn = SingletonComponent::class,
  )
  class Router2 : Router { ... }
  ```

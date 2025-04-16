# Using the Effects Library without KSP

The library can be used without the KSP annotation processor, depending on your DI setup.

You can skip KSP if:
- You're not using any DI framework
- You're using Koin

⚠️ Note: If you're using Hilt, KSP is still required.

## Table of Contents

- [Pros and Cons](#pros-and-cons)
- [Installation and Usage Example without KSP (Koin)](#installation-and-usage-example-without-ksp-koin)
- [Installation and Usage without KSP (Core, No DI Frameworks)](#installation-and-usage-without-ksp-core-no-di-frameworks)

## Pros and Cons

✅ Advantages:
- You can remove annotations like `@KoinEffect` and `@EffectClass`, they become optional.
- Faster Gradle builds, since KSP won't scan for annotations or generate code during compilation.
- Simpler Gradle setup for multi-module projects: you can skip KSP configuration entirely 
  in library modules:

  ```kotlin
  // remove this:
  ksp {
      arg("effects.processor.metadata", "generate")
  }
  ```

⚠️ Disadvantages:
- Slower injection, as proxies are created at runtime instead of compile time.
- Only supported on JVM platforms.
- Requires manual DI configuration, since your DI framework won’t be aware of effects.

## Installation and Usage Example without KSP (Koin)

1. Add the following dependencies in your `build.gradle` file:

   ```kotlin
   // required:
   implementation("com.uandcode:effects2-core-runtime:2.0.0-alpha02")
   // for projects without Jetpack Compose:
   implementation("com.uandcode:effects2-koin:2.0.0-alpha02")
   // for projects with Jetpack Compose:
   implementation("com.uandcode:effects2-koin-compose:2.0.0-alpha02")
   
   // Koin dependencies:
   implementation("io.insert-koin:koin-core:4.0.3") // core
   implementation("io.insert-koin:koin-androidx-compose:4.0.3") // android compose
   ```

2. Create a custom Application class and call `setupRuntimeEffectsGlobally()`:

   ```kotlin
   class MyApp : Application() {
       override fun onCreate() {
           super.onCreate()
           setupRuntimeEffectsGlobally()
       }
   }
   ```

3. Register the application class in `AndroidManifest.xml`:

   ```xml
   <application
       android:name=".MyApp" />
   ```

4. Define an effect interface, for example:

   ```kotlin
   interface Toasts {
       fun show(message: String)
   }
   ```

5. Create an implementation of the effect interface:

   ```kotlin
   class ToastsImpl(
       private val activity: ComponentActivity,
   ): Toasts {
       override fun show(message: String) {
           Toasts.makeText(activity, message, Toast.LENGTH_SHORT).show()
       }
   }
   ```

6. Inject the effect interface into your ViewModel:

   ```kotlin
   class CatsViewModel(
       val toasts: Toasts = RootEffectScopes.global.getProxy(),
   ): ViewModel() {
       fun removeCat(cat: Cat) {
           toasts.show("Cat $cat has been removed")
       }
   }
   ```
7. Manually define a Koin module with the effect interface:

   ```kotlin
   val myEffectsModule = module {
       effect<Toasts>()
   }
   val viewModelsModule = module {
       viewModelOf(::CatsViewModel)
   }
   ```

8. Initialize Koin, add your modules, and call `installRuntimeKoinEffects()` in the end:

   ```kotlin
   class MyApp : Application() {
       override fun onCreate() {
           super.onCreate()
           setupRuntimeEffectsGlobally()
   
           startKoin {
               androidContext(this@MyApp)
               modules(viewModelsModule, myEffectsModule)
   
               installRuntimeKoinEffects()
           }
       }
   }
   ```

9. (Optional) Scope effects manually using the `effectScope()` function:

   ```kotlin
   
   // modules:
   val myEffectsModule = module {
       effectScope<MainActivity> {
           effect<Toasts>()
       }
   }
   val viewModelsModule = module {
       scope<MainActivity> {
           viewModelOf(::CatsViewModel)
       }
   }
   
   // MainActivity:
   class MainActivity : ComponentActivity(), AndroidScopeComponent {
   
       override val scope: Scope by activityRetainedScope()
   
       // projects without Jetpack Compose
       private val toasts by lazyEffect { ToastsImpl(context = this) }
   
       // projects with Jetpack Compose
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContent {
               KoinActivityScope {
                   val toasts = remember { ToastsImpl() }
                   EffectProvider(toasts) {
                       // ...
                   }
               }
           }
       }
   }
   ```

## Installation and Usage without KSP (Core, No DI Frameworks)

1. Add the required dependencies in your `build.gradle` file:

   ```kotlin
   // required:
   implementation("com.uandcode:effects2-core-runtime:2.0.0-alpha02")
   // for projects without Jetpack Compose:
   implementation("com.uandcode:effects2-core-lifecycle:2.0.0-alpha02")
   // for projects with Jetpack Compose:
   implementation("com.uandcode:effects2-core-compose:2.0.0-alpha02")   
   ```

2. Create a custom Application class and call `setupRuntimeEffectsGlobally()`

   ```kotlin
   class MyApp : Application() {
       override fun onCreate() {
           super.onCreate()
           setupRuntimeEffectsGlobally()
       }
   }
   ```

3. Register the application class in `AndroidManifest.xml`:

   ```xml
   <application
       android:name=".MyApp" />
   ```

4. Define an effect interface, for example:

   ```kotlin
   interface Toasts {
       fun show(message: String)
   }
   ```

5. Implement the interface:

   ```kotlin
   class ToastsImpl(
       private val activity: ComponentActivity,
   ): Toasts {
       override fun show(message: String) {
           Toasts.makeText(activity, message, Toast.LENGTH_SHORT).show()
       }
   }
   ```

6. Inject the interface into your `ViewModel`:

   ```kotlin
   class CatsViewModel(
       val toasts: Toasts,
   ): ViewModel() {
       fun removeCat(cat: Cat) {
           toasts.show("Cat $cat has been removed")
       }
   }
   ```

7. Connect the implementation to the effect interface using either `lazyEffect` or `EffectProvider`:
   
   ```kotlin
   // MainActivity:
   class MainActivity : ComponentActivity() {

       // projects without Jetpack Compose
       private val toasts by lazyEffect { ToastsImpl(context = this) }

       // projects with Jetpack Compose
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContent {
               val toasts = remember { ToastsImpl() }
               EffectProvider(toasts) {
                   // ...
               }
           }
       }
   }
   ```
   

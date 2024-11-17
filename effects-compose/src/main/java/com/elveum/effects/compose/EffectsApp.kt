package com.elveum.effects.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Top level function which automatically initializes all side effects for your app.
 * Just wrap your top-level Composable function into EffectsApp to make it works.
 *
 * For example:
 *
 * ```
 * @AndroidEntryPoint
 * class MainActivity : ComponentActivity() {
 *   override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *     setContent {
 *       EffectsApp {
 *         MyApp()
 *       }
 *     }
 *   }
 * }
 *
 * @Composable
 * fun MyApp() {
 *   // your program here
 * }
 * ```
 */
@Composable
public fun EffectsApp(
    content: @Composable () -> Unit,
) {
    val effectsLifecycleController = getEffectsLifecycleController()
    ObserveLifecycleEvents(
        onStart = {
            effectsLifecycleController.startEffects()
        },
        onStop = {
            effectsLifecycleController.stopEffects()
        },
        onDestroy = {
            effectsLifecycleController.destroyEffects()
        },
    )
    content()
}

@Composable
private fun ObserveLifecycleEvents(
    onStart: () -> Unit,
    onStop: () -> Unit,
    onDestroy: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val observer = remember {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_STOP -> onStop()
                Lifecycle.Event.ON_DESTROY -> onDestroy()
                else -> {}
            }
        }
    }
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
package com.elveum.effects.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine

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
    LaunchedEffect(lifecycleOwner) {
        // Using LaunchedEffect instead of DisposableEffect because
        // LaunchedEffect starts later than DisposableEffect due to usage
        // of coroutines. As a result, effects will be delivered after
        // initialization of subsequent composable functions.
        try {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                onStart()
                suspendCancellableCoroutine { continuation ->
                    continuation.invokeOnCancellation {
                        onStop()
                    }
                }
            }
        } catch (e: CancellationException) {
            onDestroy()
            throw e
        }
    }
}
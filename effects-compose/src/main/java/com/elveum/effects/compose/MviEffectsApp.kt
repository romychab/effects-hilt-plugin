package com.elveum.effects.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Top level function which automatically initializes all MVI-effects for your app.
 *
 * Just wrap your top-level Composable function into MviEffectsApp to make it work.
 *
 * For example:
 *
 * ```
 * @AndroidEntryPoint
 * class MainActivity : ComponentActivity() {
 *   override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *     setContent {
 *       MviEffectsApp {
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
public fun MviEffectsApp(
    content: @Composable () -> Unit,
) {
    val effectsEntryPoint = getMviEffectsEntryPoint()
    val effectsStore = effectsEntryPoint.getMviEffectsStore()
    CompositionLocalProvider(
        LocalMviEffectsStore provides effectsStore
    ) {
        val effectsLifecycleController = getMviEffectsLifecycleController()
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
}

@Deprecated(
    message = "Use MviEffectsApp instead.",
    replaceWith = ReplaceWith("MviEffectsApp"),
)
@Composable
public fun EffectsApp(
    content: @Composable () -> Unit,
) {
    MviEffectsApp(content)
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
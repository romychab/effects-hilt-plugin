package com.elveum.effects.compose.v2

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
public inline fun <reified T: Any> getEffect(): T {
    return LocalEffectProvider.current.getEffect(T::class)
}

@Composable
public fun EffectProvider(
    vararg effects: Any,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val activity = context.getHostActivity()
    val composeEffectProvider = remember {
        ComposeEffectProviderImpl(effects.toList(), activity)
    }
    CompositionLocalProvider(
        LocalEffectProvider provides composeEffectProvider
    ) {
        ObserveLifecycleEvents(
            onStart = composeEffectProvider::start,
            onStop = composeEffectProvider::stop,
        )
        content()
    }
}

/**
 * Get [ComponentActivity] instance from the context.
 * @throws IllegalStateException if there is no [ComponentActivity] within the context
 */
public fun Context?.getHostActivity(): ComponentActivity {
    return when(this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.getHostActivity()
        else -> throw IllegalStateException("Can't find ComponentActivity")
    }
}

@Composable
private fun ObserveLifecycleEvents(
    onStart: () -> Unit,
    onStop: () -> Unit,
    onDestroy: () -> Unit = {},
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
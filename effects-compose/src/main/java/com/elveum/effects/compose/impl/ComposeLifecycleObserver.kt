package com.elveum.effects.compose.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
internal fun ComposeLifecycleObserver(
    key: Any,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onDestroy: () -> Unit = {},
) {
    val onStartState by rememberUpdatedState(onStart)
    val onStopState by rememberUpdatedState(onStop)
    val onDestroyState by rememberUpdatedState(onDestroy)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key, lifecycleOwner) {
        // Using LaunchedEffect instead of DisposableEffect because
        // LaunchedEffect starts later than DisposableEffect due to usage
        // of coroutines. As a result, effects will be delivered after
        // initialization of subsequent composable functions.
        try {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                onStartState()
                suspendCancellableCoroutine { continuation ->
                    continuation.invokeOnCancellation {
                        onStopState()
                    }
                }
            }
        } catch (e: CancellationException) {
            onDestroyState()
            throw e
        }
    }
}
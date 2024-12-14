package com.elveum.effects.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.elveum.effects.annotations.MviEffect
import com.elveum.effects.core.MviEffectsLifecycleController
import com.elveum.effects.core.di.MviEffectsEntryPoint
import com.elveum.effects.core.get
import dagger.hilt.android.EntryPointAccessors

/**
 * Get an effect implementation (instance of class annotated with [MviEffect] annotation).
 *
 * This function can be called only within [MviEffectsApp].
 *
 * @throws IllegalStateException if the is no such effect class
 */
@ReadOnlyComposable
@Composable
public inline fun <reified T : Any> getMviEffect(): T {
    return LocalMviEffectsStore.current.get()
}

@Deprecated(
    message = "Use getMviEffect instead.",
    replaceWith = ReplaceWith("getMviEffect")
)
@ReadOnlyComposable
@Composable
public inline fun <reified T : Any> getEffect(): T {
    return getMviEffect()
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
@ReadOnlyComposable
@Composable
public fun getMviEffectsLifecycleController(): MviEffectsLifecycleController {
    return getMviEffectsEntryPoint().getMviEffectsLifecycleController()
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
@ReadOnlyComposable
@Composable
public fun getMviEffectsEntryPoint(): MviEffectsEntryPoint {
    val context = LocalContext.current
    val activity = context.getHostActivity()
    return EntryPointAccessors.fromActivity(activity, MviEffectsEntryPoint::class.java)
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

package com.elveum.effects.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.elveum.effects.annotations.SideEffect
import com.elveum.effects.core.EffectsLifecycleController
import com.elveum.effects.core.di.EffectsEntryPoint
import com.elveum.effects.core.get
import dagger.hilt.android.EntryPointAccessors

/**
 * Get an effect implementation (instance of class annotated with [SideEffect] annotation).
 *
 * This function can be called only within [EffectsApp].
 *
 * @throws IllegalStateException if the is no such effect class
 */
@ReadOnlyComposable
@Composable
public inline fun <reified T : Any> getEffect(): T {
    return LocalEffectsStore.current.get()
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
@ReadOnlyComposable
@Composable
public fun getEffectsLifecycleController(): EffectsLifecycleController {
    return getEffectsEntryPoint().getEffectsLifecycleController()
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
@ReadOnlyComposable
@Composable
public fun getEffectsEntryPoint(): EffectsEntryPoint {
    val context = LocalContext.current
    val activity = context.getHostActivity()
    return EntryPointAccessors.fromActivity(activity, EffectsEntryPoint::class.java)
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

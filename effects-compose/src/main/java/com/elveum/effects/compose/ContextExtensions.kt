package com.elveum.effects.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

/**
 * Get [ComponentActivity] instance from the context.
 * @throws IllegalStateException if there is no [ComponentActivity] within the context
 */
public fun Context?.getHostActivity(): ComponentActivity {
    return when(this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.getHostActivity()
        else -> throw IllegalStateException("Can't find ComponentActivity. " +
                "Please, make sure your activity extends ComponentActivity.")
    }
}

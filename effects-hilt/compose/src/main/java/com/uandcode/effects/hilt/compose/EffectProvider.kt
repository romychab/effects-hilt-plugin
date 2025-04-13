package com.uandcode.effects.hilt.compose

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.uandcode.effects.core.compose.EffectProvider
import com.uandcode.effects.hilt.annotations.HiltEffect
import com.uandcode.effects.hilt.getEffectEntryPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.uandcode.effects.core.compose.EffectProvider as CoreEffectProvider
import com.uandcode.effects.core.compose.getEffect as getCoreEffect

/**
 * Get an effect implementation of a type [T] within the
 * existing [EffectProvider] component.
 *
 * @throws IllegalStateException if this method is called not within [EffectProvider]
 * @throws IllegalArgumentException if the specified effect of type [T] is not
 *                                  managed by the [EffectProvider] component
 */
@Composable
public inline fun <reified T: Any> getEffect(): T {
    return getCoreEffect()
}

/**
 * This component attaches effect implementations listed in [effects] argument to their
 * corresponding interfaces injected to view-models or other objects with longer lifecycle.
 *
 * Classes for all effects must be annotated with [HiltEffect] annotation.
 *
 * All effects listed in [effects] param are automatically attached to their
 * interfaces when a host activity is started. Afterwards, they are automatically
 * detached when the host activity is stopped.
 *
 * Usage example:
 *
 * ```
 * @Composable
 * fun Root() {
 *     val myEffectImpl1 = remember { MyEffectImpl1() }
 *     val myEffectImpl2 = remember { MyEffectImpl2() }
 *     EffectProvider(myEffectImpl1, myEffectImpl2)  {
 *         MyApp()
 *     }
 * }
 *
 * @Composable
 * fun MyApp() {
 *     // you can call getEffect() within the EffectProvider component if needed:
 *     val myEffectImpl1 = getEffect<MyEffectImpl1>()
 * }
 * ```
 *
 * EffectProvider components can be nested:
 *
 * ```
 * @Composable
 * fun Root() {
 *     val myEffectImpl1 = remember { MyEffectImpl1() }
 *     EffectProvider(myEffectImpl1)  {
 *         val myEffectImpl2 = remember { MyEffectImpl2() }
 *         EffectProvider(myEffectImpl2)  {
 *             MyApp()
 *         }
 *     }
 * }
 * ```
 *
 * @param effects the list of effect implementations managed by this EffectProvider;
 *                please note, only instances of classes annotated with [HiltEffect] annotation are allowed
 */
@Composable
public fun EffectProvider(
    effects: ImmutableList<Any>,
    content: @Composable () -> Unit,
) {
    val activity = LocalContext.current.getHostActivity()
    val effectScope = remember { activity.getEffectEntryPoint().getEffectScope() }
    CoreEffectProvider(
        effects = effects,
        scope = effectScope,
        content = content,
    )
}

/**
 * This component attaches effect implementations listed in [effects] argument to their
 * corresponding interfaces injected to view-models or other objects with longer lifecycle.
 *
 * Classes for all effects must be annotated with [HiltEffect] annotation.
 *
 * All effects listed in [effects] param are automatically attached to their
 * interfaces when a host activity is started. Afterwards, they are automatically
 * detached when the host activity is stopped.
 *
 * Usage example:
 *
 * ```
 * @Composable
 * fun Root() {
 *     val myEffectImpl1 = remember { MyEffectImpl1() }
 *     val myEffectImpl2 = remember { MyEffectImpl2() }
 *     EffectProvider(myEffectImpl1, myEffectImpl2)  {
 *         MyApp()
 *     }
 * }
 *
 * @Composable
 * fun MyApp() {
 *     // you can call getEffect() within the EffectProvider component if needed:
 *     val myEffectImpl1 = getEffect<MyEffectImpl1>()
 * }
 * ```
 *
 * EffectProvider components can be nested:
 *
 * ```
 * @Composable
 * fun Root() {
 *     val myEffectImpl1 = remember { MyEffectImpl1() }
 *     EffectProvider(myEffectImpl1)  {
 *         val myEffectImpl2 = remember { MyEffectImpl2() }
 *         EffectProvider(myEffectImpl2)  {
 *             MyApp()
 *         }
 *     }
 * }
 * ```
 *
 * @param effects the list of effect implementations managed by this EffectProvider;
 *                please note, only instances of classes annotated with [HiltEffect] annotation are allowed
 */
@Composable
public fun EffectProvider(
    vararg effects: Any,
    content: @Composable () -> Unit,
): Unit = EffectProvider(effects.toImmutableList(), content)

private fun Context?.getHostActivity(): ComponentActivity {
    return when(this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.getHostActivity()
        else -> throw IllegalStateException("Can't find ComponentActivity. " +
                "Please, make sure your activity extends ComponentActivity.")
    }
}

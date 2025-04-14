package com.uandcode.effects.koin.compose

import androidx.compose.runtime.Composable
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.compose.EffectProvider
import com.uandcode.effects.koin.annotations.KoinEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.currentKoinScope
import org.koin.core.scope.Scope
import com.uandcode.effects.core.compose.EffectProvider as CoreEffectProvider
import com.uandcode.effects.core.compose.getEffect as getCoreEffect

/**
 * Get an effect implementation of a type [T] within the
 * existing [EffectProvider] component.
 */
@Composable
public inline fun <reified T: Any> getEffect(): T {
    return getCoreEffect()
}

/**
 * This component attaches effect implementations listed in [effects] argument to their
 * corresponding interfaces injected to view-models or other objects with longer lifecycle.
 *
 * Classes for all effects must be annotated with [KoinEffect] annotation.
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
 *                please note, only instances of classes annotated with [KoinEffect] annotation are allowed
 */
@Composable
public fun EffectProvider(
    effects: ImmutableList<Any>,
    scope: Scope = currentKoinScope(),
    content: @Composable () -> Unit,
) {
    val effectScope = scope.get<EffectScope>()
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
 * Classes for all effects must be annotated with [KoinEffect] annotation.
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
 *                please note, only instances of classes annotated with [KoinEffect] annotation are allowed
 */
@Composable
public fun EffectProvider(
    vararg effects: Any,
    scope: Scope = currentKoinScope(),
    content: @Composable () -> Unit,
): Unit = EffectProvider(effects.toImmutableList(), scope, content)

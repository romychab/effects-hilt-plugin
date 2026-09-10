package com.uandcode.effects.koin.compose

import androidx.compose.runtime.Composable
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.koin.annotations.KoinEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.currentKoinScope
import org.koin.core.scope.Scope
import com.uandcode.effects.core.compose.EffectProvider as CoreEffectProvider
import com.uandcode.effects.core.compose.getEffect as getCoreEffect

/**
 * Get an effect implementation of a type [T] within the content block
 * of the existing [EffectProvider] component.
 *
 * Please note, the [EffectProvider] content block is mandatory here. Effects
 * provided by a leaf `EffectProvider(...)` call without a content block are
 * attached to their interfaces, but they are not visible for this function.
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
 * The content is optional. If you don't need to call [getEffect] within nested
 * composables, you can use EffectProvider as a leaf component:
 *
 * ```
 * @Composable
 * fun Root() {
 *     val myEffectImpl1 = remember { MyEffectImpl1() }
 *     EffectProvider(myEffectImpl1)
 *     MyApp()
 * }
 * ```
 *
 * Effects are attached to their interfaces in both cases. The difference is the
 * visibility of effects for the [getEffect] function: it works only within the
 * content block. In the example above, a `getEffect()` call inside `MyApp()`
 * throws [IllegalStateException], because `MyApp()` is a sibling of the
 * EffectProvider component and not a part of its content.
 *
 * @param effects the list of effect implementations managed by this EffectProvider;
 *                please note, only instances of classes annotated with [KoinEffect] annotation are allowed
 * @param scope the Koin [Scope] used for resolving the [EffectScope] which holds effect
 *              interfaces managed by this EffectProvider; by default, the current Koin scope is used
 * @param content the optional content of this EffectProvider; effects listed in [effects]
 *                param are attached to their interfaces even if the content is omitted,
 *                but [getEffect] can be called only within the content block
 */
@Composable
public fun EffectProvider(
    effects: ImmutableList<Any>,
    scope: Scope = currentKoinScope(),
    content: (@Composable () -> Unit)? = null,
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
 * The content is optional. If you don't need to call [getEffect] within nested
 * composables, you can use EffectProvider as a leaf component:
 *
 * ```
 * @Composable
 * fun Root() {
 *     val myEffectImpl1 = remember { MyEffectImpl1() }
 *     EffectProvider(myEffectImpl1)
 *     MyApp()
 * }
 * ```
 *
 * Effects are attached to their interfaces in both cases. The difference is the
 * visibility of effects for the [getEffect] function: it works only within the
 * content block. In the example above, a `getEffect()` call inside `MyApp()`
 * throws [IllegalStateException], because `MyApp()` is a sibling of the
 * EffectProvider component and not a part of its content.
 *
 * @param effects the list of effect implementations managed by this EffectProvider;
 *                please note, only instances of classes annotated with [KoinEffect] annotation are allowed
 * @param scope the Koin [Scope] used for resolving the [EffectScope] which holds effect
 *              interfaces managed by this EffectProvider; by default, the current Koin scope is used
 * @param content the optional content of this EffectProvider; effects listed in [effects]
 *                param are attached to their interfaces even if the content is omitted,
 *                but [getEffect] can be called only within the content block
 */
@Composable
public fun EffectProvider(
    vararg effects: Any,
    scope: Scope = currentKoinScope(),
    content: (@Composable () -> Unit)? = null,
): Unit = EffectProvider(effects.toImmutableList(), scope, content)

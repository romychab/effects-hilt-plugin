package com.uandcode.effects.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.RootEffectScopes
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.effects.core.compose.impl.ComposeEffectNodeImpl
import com.uandcode.effects.core.compose.impl.ComposeLifecycleObserver
import com.uandcode.effects.core.compose.impl.FakeComposeEffectNode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * Can be used in tests for replacing real effect providers.
 * @see FakeComposeEffectNode
 */
public val LocalComposeEffectNode: ProvidableCompositionLocal<ComposeEffectNode?> =
    compositionLocalOf { null }

/**
 * Provides the [EffectScope] for the current composition.
 * By default, it returns [RootEffectScopes.global] scope.
 */
public val LocalEffectScope: ProvidableCompositionLocal<EffectScope> =
    compositionLocalOf { RootEffectScopes.global }

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
    return LocalComposeEffectNode.current?.getEffect(T::class)
        ?: throw IllegalStateException("getEffect() can be called only within EffectProvider { ... }")
}

/**
 * This component attaches effect implementations listed in [effects] argument to their
 * corresponding interfaces injected to view-models or other objects with longer lifecycle.
 *
 * Classes for all effects must be annotated with [EffectClass] annotation.
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
 *                please note, only instances of classes annotated with [EffectClass] annotation are allowed
 */
@Composable
public fun EffectProvider(
    effects: ImmutableList<Any>,
    scope: EffectScope = LocalEffectScope.current,
    content: @Composable () -> Unit,
) {
    val currentComposeEffectNode = LocalComposeEffectNode.current
    val newComposeEffectNode = remember(effects) {
        currentComposeEffectNode?.attachNode(effects)
            ?: ComposeEffectNodeImpl(effects, scope)
    }
    CompositionLocalProvider(
        LocalComposeEffectNode provides newComposeEffectNode,
        LocalEffectScope provides scope,
    ) {
        ComposeLifecycleObserver(
            onStart = newComposeEffectNode::start,
            onStop = newComposeEffectNode::stop,
        )
        content()
    }
}

/**
 * This component attaches effect implementations listed in [effects] argument to their
 * corresponding interfaces injected to view-models or other objects with longer lifecycle.
 *
 * Classes for all effects must be annotated with [EffectClass] annotation.
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
 *                please note, only instances of classes annotated with [EffectClass] annotation are allowed
 */
@Composable
public fun EffectProvider(
    vararg effects: Any,
    scope: EffectScope = LocalEffectScope.current,
    content: @Composable () -> Unit,
) {
    val effectsList = effects.toImmutableList()
    EffectProvider(
        effects = effectsList,
        scope = scope,
        content = content,
    )
}


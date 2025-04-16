package com.elveum.effects.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.elveum.effects.compose.impl.ComposeEffectNodeImpl
import com.elveum.effects.compose.impl.ComposeLifecycleObserver
import com.elveum.effects.compose.impl.FakeComposeEffectNode

/**
 * Can be used in tests for replacing real effect providers.
 * @see FakeComposeEffectNode
 */
public val LocalComposeEffectNode: ProvidableCompositionLocal<ComposeEffectNode?> =
    compositionLocalOf { null }

/**
 * Get an effect implementation of a type [T].
 *
 * @throws IllegalStateException if this method is called not within [EffectProvider]
 * @throws IllegalArgumentException if the specified effect of type [T] is not
 *                                  provided by [EffectProvider]
 */
@Composable
public inline fun <reified T: Any> getEffect(): T {
    return LocalComposeEffectNode.current?.getEffect(T::class)
        ?: throw IllegalStateException("getEffect() can be called only within EffectProvider { ... }")
}

/**
 * A component for attaching effect implementations to corresponding target interfaces.
 *
 * All effects listed in [effects] param are automatically attached to their
 * interfaces when a host activity is started, and then they are automatically
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
 *     // you can call getEffect() within EffectProvider if needed:
 *     val myEffectImpl1 = getEffect<MyEffectImpl1>()
 * }
 * ```
 *
 * Also, EffectProvider components can be nested if needed:
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
 * @param effects the list of effect implementations provided by this EffectProvider;
 *                please note, only instances of classes annotated with [HiltEffect] are allowed
 */
@Composable
public fun EffectProvider(
    vararg effects: Any,
    content: @Composable () -> Unit,
) {
    val activity = LocalContext.current.getHostActivity()
    val currentComposeEffectNode = LocalComposeEffectNode.current
    val effectList = effects.toList()
    val newComposeEffectNode = remember(effectList) {
        currentComposeEffectNode?.attachNode(effectList)
            ?: ComposeEffectNodeImpl(effectList, activity)
    }
    CompositionLocalProvider(
        LocalComposeEffectNode provides newComposeEffectNode,
    ) {
        ComposeLifecycleObserver(
            key = newComposeEffectNode,
            onStart = newComposeEffectNode::start,
            onStop = newComposeEffectNode::stop,
        )
        content()
    }
}


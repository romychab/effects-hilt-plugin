package com.uandcode.effects.core.compose.impl

import com.uandcode.effects.core.compose.ComposeEffectNode
import kotlin.reflect.KClass

/**
 * Replace real effect implementations by fake ones.
 *
 * Usually it can be helpful in unit tests.
 *
 * Usage example:
 *
 * ```
 * val fakeEffect = remember { MyFakeEffect() }
 * CompositionLocalProvider(
 *     LocalComposeEffectNode provides FakeComposeEffectNode(fakeEffect)
 * ) {
 *     // ...
 * }
 * ```
 */
public class FakeComposeEffectNode(
    fakeEffects: List<Any>,
) : ComposeEffectNode {

    private var allEffects: List<Any> = fakeEffects
    private val replacedEffects = mutableMapOf<KClass<*>, Any>()

    override fun <T : Any> getEffect(clazz: KClass<T>): T {
        val replacedEffect = replacedEffects[clazz]
        if (replacedEffect != null) return replacedEffect as T
        return allEffects.firstOrNull { clazz.isInstance(it) } as? T
            ?: throw IllegalStateException("Effect ${clazz.simpleName} not found")
    }

    override fun attachNode(effects: List<Any>): ComposeEffectNode {
        allEffects = effects + allEffects
        return this
    }

    public fun <T : Any> replaceEffect(clazz: KClass<T>, effect: T) {
        replacedEffects[clazz] = effect
    }

    override fun start(): Unit = Unit
    override fun stop(): Unit = Unit
}

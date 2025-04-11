package com.uandcode.effects.core.compose.impl

import androidx.compose.runtime.RememberObserver
import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.bind
import com.uandcode.effects.core.compose.ComposeEffectNode
import kotlin.reflect.KClass

internal class ComposeEffectNodeImpl(
    private val managedEffects: List<Any>,
    private val effectScope: EffectScope,
    private val previousNode: ComposeEffectNodeImpl? = null,
) : ComposeEffectNode, RememberObserver {

    private val effects: List<Any> = managedEffects + (previousNode?.effects ?: emptyList())

    private val effectControllers by lazy {
        managedEffects.map { effectImplementation ->
            effectScope
                .getController(effectImplementation::class as KClass<Any>)
                .bind { effectImplementation }
        }
    }

    override fun <T : Any> getEffect(clazz: KClass<T>): T {
        val effect = effects.firstOrNull { clazz.isInstance(it) } as? T
        return effect ?: throw IllegalArgumentException("Can't find effect ${clazz.simpleName}. " +
                "Have you added it in EffectProvider(...) {}")
    }

    override fun attachNode(effects: List<Any>): ComposeEffectNode {
        return ComposeEffectNodeImpl(
            previousNode = this,
            managedEffects = effects,
            effectScope = effectScope,
        )
    }

    override fun start() {
        effectControllers.forEach(BoundEffectController<*>::start)
    }

    override fun stop() {
        effectControllers.forEach(BoundEffectController<*>::stop)
    }

    override fun onAbandoned() = stop()
    override fun onForgotten() = stop()
    override fun onRemembered() = Unit
}

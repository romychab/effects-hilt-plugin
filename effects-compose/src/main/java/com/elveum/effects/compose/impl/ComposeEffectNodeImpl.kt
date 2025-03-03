package com.elveum.effects.compose.impl

import androidx.activity.ComponentActivity
import androidx.compose.runtime.RememberObserver
import com.elveum.effects.compose.ComposeEffectNode
import com.elveum.effects.core.BoundEffectController
import com.elveum.effects.core.EffectStore
import com.elveum.effects.core.di.getEffectEntryPoint
import com.elveum.effects.core.getBoundEffectController
import kotlin.reflect.KClass

internal class ComposeEffectNodeImpl(
    private val managedEffects: List<Any>,
    private val activity: ComponentActivity,
    private val previousNode: ComposeEffectNodeImpl? = null,
) : ComposeEffectNode, RememberObserver {

    private val effects: List<Any> = managedEffects + (previousNode?.effects ?: emptyList())

    private val effectStore: EffectStore by lazy {
        previousNode?.effectStore ?: activity.getEffectEntryPoint().getEffectStore()
    }

    private val effectControllers by lazy {
        managedEffects.map { effectImplementation ->
            effectStore.getBoundEffectController(effectImplementation)
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
            activity = activity,
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

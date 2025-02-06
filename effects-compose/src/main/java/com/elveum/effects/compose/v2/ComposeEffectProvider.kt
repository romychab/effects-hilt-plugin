package com.elveum.effects.compose.v2

import androidx.activity.ComponentActivity
import com.elveum.effects.core.v2.BoundEffectController
import com.elveum.effects.core.v2.EffectStore
import com.elveum.effects.core.v2.getBoundEffectController
import com.elveum.effects.core.v2.di.getEffectEntryPoint
import kotlin.reflect.KClass

public interface ComposeEffectProvider {
    public fun <T : Any> getEffect(clazz: KClass<T>): T
}

internal class ComposeEffectProviderImpl(
    private val allEffects: List<Any>,
    private val activity: ComponentActivity,
) : ComposeEffectProvider {

    private val store: EffectStore by lazy {
        activity.getEffectEntryPoint().getEffectStore()
    }

    private val effectControllers by lazy {
        allEffects.map { effectImplementation ->
            store.getBoundEffectController(effectImplementation)
        }
    }

    override fun <T : Any> getEffect(clazz: KClass<T>): T {
        val effect = allEffects.firstOrNull { clazz.isInstance(it) } as? T
        return effect ?: throw IllegalArgumentException("Can't find effect ${clazz.simpleName}. " +
                "Have you added it in EffectProvider() { ... }")
    }

    fun start() {
        effectControllers.forEach(BoundEffectController<*>::start)
    }

    fun stop() {
        effectControllers.forEach(BoundEffectController<*>::stop)
    }

}

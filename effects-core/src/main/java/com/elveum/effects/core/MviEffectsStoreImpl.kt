package com.elveum.effects.core

import com.elveum.effects.core.actors.MviEffectImplementation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlin.reflect.KClass

@ActivityScoped
internal class MviEffectsStoreImpl @Inject constructor(
    private val implementations: Set<@JvmSuppressWildcards MviEffectImplementation>
) : MviEffectsStore {

    private val implementationsMap by lazy(LazyThreadSafetyMode.NONE) {
        mutableMapOf<KClass<*>, MviEffectImplementation>().apply {
            implementations.forEach { implementation ->
                put(implementation.instance::class, implementation)
                findInterfaceClass(implementation.target)?.let {
                    put(it, implementation)
                }
            }
        }
    }

    override fun <T : Any> get(clazz: KClass<T>): T {
        return implementationsMap[clazz]?.instance as? T
            ?: throw IllegalStateException("Can't find MviEffect class")
    }

    private fun findInterfaceClass(target: String): KClass<*>? {
        return try {
            val javaClass = Class.forName(target)
            javaClass.kotlin
        } catch (ignored: ClassNotFoundException) {
            null
        }
    }

}

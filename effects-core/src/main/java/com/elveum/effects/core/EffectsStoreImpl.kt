package com.elveum.effects.core

import com.elveum.effects.core.actors.SideEffectImplementation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlin.reflect.KClass

@ActivityScoped
internal class EffectsStoreImpl @Inject constructor(
    private val implementations: Set<@JvmSuppressWildcards SideEffectImplementation>
) : EffectsStore {

    private val implementationsMap by lazy(LazyThreadSafetyMode.NONE) {
        implementations.associateBy { implementation -> implementation.instance::class }
    }

    override fun <T : Any> get(clazz: KClass<T>): T {
        return implementationsMap[clazz]?.instance as? T
            ?: throw IllegalStateException("Can't find SideEffect class")
    }

}
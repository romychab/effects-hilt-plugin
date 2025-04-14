@file:OptIn(KoinInternalApi::class)

package com.uandcode.effects.koin

import org.koin.core.annotation.KoinInternalApi
import org.koin.dsl.ScopeDSL
import kotlin.reflect.KClass

public class KoinEffectScopeBuilder(
    public val scopeDSL: ScopeDSL,
) {

    @KoinInternalApi
    public val registeredEffects: MutableSet<KClass<*>> = mutableSetOf()

    /**
     * Register the specified scoped effect interface [T].
     */
    public inline fun <reified T : Any> effect() {
        scopeDSL.effect<T>()
        registeredEffects.add(T::class)
    }

}

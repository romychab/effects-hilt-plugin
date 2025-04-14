@file:OptIn(KoinInternalApi::class)

package com.uandcode.effects.koin

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.koin.exceptions.DuplicateEffectScopeException
import com.uandcode.effects.koin.internal.internalSetupScopedEffects
import com.uandcode.effects.koin.internal.validateEffectScopeQualifier
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.ScopeDSL
import java.util.concurrent.atomic.AtomicInteger

private var idSequence = AtomicInteger(0)

/**
 * Declare an effect interface definition for the specified interface [T].
 */
public inline fun <reified T : Any> Module.effect() {
    factory<T> {
        val effectScope = get<EffectScope>()
        effectScope.getProxy(T::class)
    }
}

/**
 * Declare a scoped effect interface definition for the specified interface [T].
 */
public inline fun <reified T : Any> ScopeDSL.effect() {
    // EffectScope is scoped itself, so we can use factory here
    factory<T> {
        val effectScope = get<EffectScope>()
        effectScope.getProxy(T::class)
    }
}

/**
 * Declare a scope definition which can contain scoped effects.
 * Please note, you can declare only one `effectScope` for one scope qualifier per
 * Koin Application.
 *
 * @throws DuplicateEffectScopeException
 */
public fun Module.effectScope(
    qualifier: Qualifier,
    definition: KoinEffectScopeBuilder.() -> Unit,
) {
    val id = idSequence.incrementAndGet()
    validateEffectScopeQualifier(qualifier, id)
    scope(qualifier) {
        val builder = KoinEffectScopeBuilder(this).apply(definition)
        internalSetupScopedEffects(
            key = "com.uandcode.effects.koin.effectScope-$id",
            managedInterfaces = ManagedInterfaces.ListOf(*builder.registeredEffects.toTypedArray())
        )
    }
}

/**
 * Declare a scope definition which can contain scoped effects.
 * Please note, you can declare only one `effectScope` for one scope qualifier per
 * Koin Application.
 *
 * @throws DuplicateEffectScopeException
 */
public inline fun <reified T> Module.effectScope(
    noinline definition: KoinEffectScopeBuilder.() -> Unit,
) {
    effectScope(TypeQualifier(T::class), definition)
}

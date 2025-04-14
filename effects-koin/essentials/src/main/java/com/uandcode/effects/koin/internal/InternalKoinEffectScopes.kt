package com.uandcode.effects.koin.internal

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.RootEffectScopes
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module

public val KOIN_EFFECT_ROOT_QUALIFIER: StringQualifier = named("__koin_effect_scope_root_qualifier__")

internal data class LocalEffectQualifier(val value: String)

/**
 * For internal usage. Public modifier is added to make this function available
 * from auto-generated code.
 */
public fun Module.internalSetupRootEffects() {
    single<LocalEffectQualifier> {
        LocalEffectQualifier(KOIN_EFFECT_ROOT_QUALIFIER.value)
    }
    single(KOIN_EFFECT_ROOT_QUALIFIER) {
        RootEffectScopes.global
    }
    factory<EffectScope> {
        get(KOIN_EFFECT_ROOT_QUALIFIER)
    }
}

/**
 * For internal usage. Public modifier is added to make this function available
 * from auto-generated code.
 */
public fun ScopeDSL.internalSetupScopedEffects(
    key: String,
    managedInterfaces: ManagedInterfaces,
) {
    scoped<LocalEffectQualifier> {
        LocalEffectQualifier(key)
    }
    scoped(named(key)) {
        val allQualifiers = getUniquePrioritizedQualifiers()
        // 0-index = me, 1-index = parent
        val parentQualifier = allQualifiers.getOrNull(1)
        val parentEffectScope = if (parentQualifier != null) {
            get<EffectScope>(named(parentQualifier.value))
        } else {
            RootEffectScopes.global
        }
        parentEffectScope.createChild(managedInterfaces)
    }
    scoped<EffectScope> {
        get(named(key))
    }
}

internal fun KoinApplication.installRootEffects() {
    modules(
        module {
            internalSetupRootEffects()
        }
    )
}

private fun Scope.getUniquePrioritizedQualifiers(): List<LocalEffectQualifier> {
    return getAll<LocalEffectQualifier>()
        .reversed()
        .distinct()
        .reversed()
}

package com.uandcode.effects.koin.internal

import com.uandcode.effects.koin.exceptions.DuplicateEffectScopeException
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal class EffectScopeValidator(
    private val qualifiers: MutableSet<Qualifier> = mutableSetOf(),
) {
    fun add(qualifier: Qualifier) {
        if (!qualifiers.add(qualifier)) {
            throw DuplicateEffectScopeException(qualifier)
        }
    }
}

internal fun Module.validateEffectScopeQualifier(qualifier: Qualifier, id: Int) {
    single(
        createdAtStart = true,
        qualifier = named("effectScope-$qualifier-$id")
    ) {
        get<EffectScopeValidator>().apply { add(qualifier) }
    }
}

internal fun KoinApplication.registerEffectScopeValidator() {
    modules(
        module {
            single<EffectScopeValidator> { EffectScopeValidator() }
        }
    )
}

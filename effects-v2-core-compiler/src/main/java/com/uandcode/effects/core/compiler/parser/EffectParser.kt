package com.uandcode.effects.core.compiler.parser

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.uandcode.effects.core.compiler.api.EffectExtension
import com.uandcode.effects.core.compiler.api.data.ParsedEffect
import com.uandcode.effects.core.compiler.api.extensions.KSClassDeclarationWrapper

internal fun parseEffects(resolver: Resolver, effectExtension: EffectExtension): Sequence<ParsedEffect> {
    val annotatedClasses = resolver.getSymbolsWithAnnotation(effectExtension.effectAnnotation.canonicalName)
    return annotatedClasses
        .filterIsInstance<KSClassDeclaration>()
        .map {
            effectExtension.parseEffect(KSClassDeclarationWrapper(it))
        }
}

package com.uandcode.effects.compiler.common.parser

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.uandcode.effects.compiler.common.api.EffectExtension
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper

internal fun parseEffects(
    resolver: Resolver,
    effectExtension: EffectExtension,
): Sequence<ParsedEffect> {
    val annotatedClasses = resolver.getSymbolsWithAnnotation(effectExtension.effectAnnotation.canonicalName)
    return annotatedClasses
        .filterIsInstance<KSClassDeclaration>()
        .map {
            effectExtension.parseEffect(KSClassDeclarationWrapper(it))
        }
}

package com.elveum.effects.processor.parser

import com.elveum.effects.processor.data.Const
import com.elveum.effects.processor.data.EffectInfo
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun parseEffects(resolver: Resolver): Sequence<EffectInfo> {
    val annotatedClasses = resolver.getSymbolsWithAnnotation(
        Const.HiltEffectAnnotationName.canonicalName
    )
    return annotatedClasses
        .filterIsInstance<KSClassDeclaration>()
        .map(::EffectInfo)
}

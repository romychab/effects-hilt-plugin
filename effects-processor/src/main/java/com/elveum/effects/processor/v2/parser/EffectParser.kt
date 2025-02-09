package com.elveum.effects.processor.v2.parser

import com.elveum.effects.processor.v2.data.Const
import com.elveum.effects.processor.v2.data.EffectInfo
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun parseEffects(resolver: Resolver): Sequence<EffectInfo> {
    val annotatedClasses = resolver.getSymbolsWithAnnotation(
        Const.CustomEffectAnnotationName.canonicalName
    )
    return annotatedClasses
        .filterIsInstance<KSClassDeclaration>()
        .map(::EffectInfo)
}

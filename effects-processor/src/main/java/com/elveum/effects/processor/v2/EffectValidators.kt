package com.elveum.effects.processor.v2

import com.elveum.effects.processor.v2.data.EffectInfo
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind

fun validateEffects(effects: Sequence<EffectInfo>) {
    return effects.forEach(EffectInfo::validateEffect)
}

private fun EffectInfo.validateEffect() {
    validateSymbolIsClassOrObject()
    validateSymbolIsNotAbstract()
    validateNoGenerics()
}

private fun EffectInfo.validateSymbolIsClassOrObject() {
    val classKind = effectClassDeclaration.classKind
    if (classKind != ClassKind.CLASS && classKind != ClassKind.OBJECT) {
        throw EffectKspException(
            "Symbol annotated with @${effectAnnotation.simpleName} should be a Class or Object",
            effectClassDeclaration,
        )
    }
}

private fun EffectInfo.validateSymbolIsNotAbstract() {
    if (effectClassDeclaration.isAbstract()) {
        throw EffectKspException(
            "Class annotated with @${effectAnnotation.simpleName} should not be abstract",
            effectClassDeclaration,
        )
    }
}

private fun EffectInfo.validateNoGenerics() {
    if (effectClassDeclaration.typeParameters.isNotEmpty()) {
        throw EffectKspException(
            "Class annotated with @${effectAnnotation.simpleName} should not have type parameters" ,
            effectClassDeclaration.typeParameters.first(),
        )
    }
}

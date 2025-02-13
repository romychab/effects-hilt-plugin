package com.elveum.effects.processor.v2

import com.elveum.effects.processor.v2.data.EffectInfo
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.Modifier

fun validateEffects(effects: Sequence<EffectInfo>) {
    effects.forEach(EffectInfo::validateEffect)
    effects.validateEachEffectHasSingleImplementation()
}

private fun EffectInfo.validateEffect() {
    validateSymbolIsClassOrObject()
    validateSymbolIsNotAbstract()
    validateClassDoesNotHaveTypeParameters()
    validateInterfaceDoesNotHaveTypeParameters()
    validateClassIsNotNested()
    validateInterfaceIsNotNested()
}

private fun Sequence<EffectInfo>.validateEachEffectHasSingleImplementation() {
    val groupedEffects = groupBy { it.targetInterface }
    groupedEffects.entries.forEach { (interfaceDeclaration, effects) ->
        if (effects.size > 1) {
            throw MultipleEffectImplementationsException(
                targetInterface = interfaceDeclaration,
                allImplementations = effects.map { it.effectClassDeclaration },
            )
        }
    }
}

private fun EffectInfo.validateInterfaceDoesNotHaveTypeParameters() {
    if (targetInterface.typeParameters.isNotEmpty()) {
        throw InterfaceWithTypeParametersException(
            effectAnnotation,
            targetInterfaceName,
            effectClassDeclaration,
        )
    }
}

private fun EffectInfo.validateClassIsNotNested() {
    if (effectClassDeclaration.parentDeclaration != null) {
        throw NestedClassException(
            effectAnnotation,
            effectClassDeclaration,
        )
    }
}

private fun EffectInfo.validateInterfaceIsNotNested() {
    if (targetInterface.parentDeclaration != null) {
        throw NestedInterfaceException(
            effectAnnotation,
            effectClassDeclaration,
        )
    }
}

private fun EffectInfo.validateSymbolIsClassOrObject() {
    val classKind = effectClassDeclaration.classKind
    val modifiers = effectClassDeclaration.modifiers
    if (modifiers.contains(Modifier.SEALED)) {
        throw InvalidClassTypeException(effectAnnotation, effectClassDeclaration)
    }
    if (classKind != ClassKind.CLASS
        && classKind != ClassKind.OBJECT) {
        throw InvalidClassTypeException(effectAnnotation, effectClassDeclaration)
    }
}

private fun EffectInfo.validateSymbolIsNotAbstract() {
    if (effectClassDeclaration.isAbstract()) {
        throw ClassIsAbstractException(effectAnnotation, effectClassDeclaration)
    }
}

private fun EffectInfo.validateClassDoesNotHaveTypeParameters() {
    if (effectClassDeclaration.typeParameters.isNotEmpty()) {
        throw ClassWithTypeParametersException(
            effectAnnotation,
            effectClassDeclaration.typeParameters.first(),
        )
    }
}
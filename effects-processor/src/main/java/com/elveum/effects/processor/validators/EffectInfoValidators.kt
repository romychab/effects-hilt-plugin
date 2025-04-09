package com.elveum.effects.processor.validators

import com.elveum.effects.processor.data.EffectInfo
import com.elveum.effects.processor.exceptions.ClassIsAbstractException
import com.elveum.effects.processor.exceptions.ClassWithTypeParametersException
import com.elveum.effects.processor.exceptions.InterfaceWithTypeParametersException
import com.elveum.effects.processor.exceptions.InvalidClassTypeException
import com.elveum.effects.processor.exceptions.NestedClassException
import com.elveum.effects.processor.exceptions.NestedInterfaceException
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.Modifier

fun validateEffects(effects: Sequence<EffectInfo>) {
    effects.forEach(EffectInfo::validateEffect)
}

private fun EffectInfo.validateEffect() {
    validateSymbolIsClassOrObject()
    validateSymbolIsNotAbstract()
    validateClassDoesNotHaveTypeParameters()
    validateInterfaceDoesNotHaveTypeParameters()
    validateClassIsNotNested()
    validateInterfaceIsNotNested()
}

private fun EffectInfo.validateInterfaceDoesNotHaveTypeParameters() {
    targetInterfaceList.forEach { targetInterface ->
        if (targetInterface.typeParameters.isNotEmpty()) {
            throw InterfaceWithTypeParametersException(
                effectAnnotation,
                targetInterface.simpleNameText,
                effectClassDeclaration,
            )
        }
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
    targetInterfaceList.forEach { targetInterface ->
        if (targetInterface.parentDeclaration != null) {
            throw NestedInterfaceException(
                effectAnnotation,
                effectClassDeclaration,
            )
        }
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
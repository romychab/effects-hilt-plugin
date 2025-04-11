package com.uandcode.effects.compiler.common.validators

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.Modifier
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.exceptions.ClassIsAbstractException
import com.uandcode.effects.compiler.common.exceptions.ClassWithTypeParametersException
import com.uandcode.effects.compiler.common.exceptions.InterfaceWithTypeParametersException
import com.uandcode.effects.compiler.common.exceptions.InvalidClassTypeException
import com.uandcode.effects.compiler.common.exceptions.NestedClassException
import com.uandcode.effects.compiler.common.exceptions.NestedInterfaceException

internal fun validateEffects(effects: Sequence<ParsedEffect>) {
    effects.forEach(ParsedEffect::validateEffect)
}

private fun ParsedEffect.validateEffect() {
    validateSymbolIsClassOrObject()
    validateSymbolIsNotAbstract()
    validateClassDoesNotHaveTypeParameters()
    validateInterfaceDoesNotHaveTypeParameters()
    validateClassIsNotNested()
    validateInterfaceIsNotNested()
}

private fun ParsedEffect.validateInterfaceDoesNotHaveTypeParameters() {
    targetInterfaces.forEach { targetInterface ->
        if (targetInterface.typeParameters.isNotEmpty()) {
            throw InterfaceWithTypeParametersException(
                effectAnnotation,
                targetInterface.simpleNameText,
                classDeclaration,
            )
        }
    }
}

private fun ParsedEffect.validateClassIsNotNested() {
    if (classDeclaration.parentDeclaration != null) {
        throw NestedClassException(effectAnnotation, classDeclaration)
    }
}

private fun ParsedEffect.validateInterfaceIsNotNested() {
    targetInterfaces.forEach { targetInterface ->
        if (targetInterface.parentDeclaration != null) {
            throw NestedInterfaceException(effectAnnotation, classDeclaration)
        }
    }
}

private fun ParsedEffect.validateSymbolIsClassOrObject() {
    val classKind = classDeclaration.classKind
    val modifiers = classDeclaration.modifiers
    if (modifiers.contains(Modifier.SEALED)) {
        throw InvalidClassTypeException(effectAnnotation, classDeclaration)
    }
    if (classKind != ClassKind.CLASS
        && classKind != ClassKind.OBJECT) {
        throw InvalidClassTypeException(effectAnnotation, classDeclaration)
    }
}

private fun ParsedEffect.validateSymbolIsNotAbstract() {
    if (classDeclaration.isAbstract()) {
        throw ClassIsAbstractException(effectAnnotation, classDeclaration)
    }
}

private fun ParsedEffect.validateClassDoesNotHaveTypeParameters() {
    if (classDeclaration.typeParameters.isNotEmpty()) {
        throw ClassWithTypeParametersException(
            effectAnnotation,
            classDeclaration.typeParameters.first(),
        )
    }
}

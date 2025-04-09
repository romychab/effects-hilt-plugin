package com.uandcode.effects.core.compiler.validators

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.Modifier
import com.uandcode.effects.core.compiler.api.data.ParsedEffect
import com.uandcode.effects.core.compiler.exceptions.ClassIsAbstractException
import com.uandcode.effects.core.compiler.exceptions.ClassWithTypeParametersException
import com.uandcode.effects.core.compiler.exceptions.InterfaceWithTypeParametersException
import com.uandcode.effects.core.compiler.exceptions.InvalidClassTypeException
import com.uandcode.effects.core.compiler.exceptions.NestedClassException
import com.uandcode.effects.core.compiler.exceptions.NestedInterfaceException

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

package com.elveum.effects.processor.v2.extensions

import com.elveum.effects.processor.v2.EffectKspException
import com.elveum.effects.processor.v2.InvalidTargetArgumentException
import com.elveum.effects.processor.v2.TargetInterfaceIsNotSpecifiedException
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ksp.toClassName

internal fun KSAnnotationWrapper.findClassDeclaration(argumentName: String): KSClassDeclaration {
    val argument = arguments.first { it.name?.asString() == argumentName }
    val argumentValue = argument.value as? KSType
        ?: throw InvalidTargetArgumentException(this)
    validateTargetArgumentExists(argumentValue)
    return argumentValue.declaration as? KSClassDeclaration
        ?: throw InvalidTargetArgumentException(this)
}

private fun KSAnnotationWrapper.validateTargetArgumentExists(argumentValue: KSType) {
    if (argumentValue.toClassName() == ANY) {
        throw TargetInterfaceIsNotSpecifiedException(this)
    }
}

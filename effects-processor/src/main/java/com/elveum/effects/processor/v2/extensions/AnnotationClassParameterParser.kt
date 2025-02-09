package com.elveum.effects.processor.v2.extensions

import com.elveum.effects.processor.v2.EffectKspException
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ksp.toClassName

internal fun KSAnnotationWrapper.findClassDeclaration(argumentName: String): KSClassDeclaration {
    val argument = arguments.first { it.name?.asString() == argumentName }
    val argumentValue = argument.value as? KSType
        ?: throwInvalidClassParameterException()
    validateTargetArgumentExists(argumentValue)
    return argumentValue.declaration as? KSClassDeclaration
        ?: throwInvalidClassParameterException()
}

private fun KSAnnotationWrapper.validateTargetArgumentExists(argumentValue: KSType) {
    if (argumentValue.toClassName() == ANY) {
        throw EffectKspException(
            "$printableName(target = ...) parameter should be specified if your class " +
                    "implements more than 1 interface",
            this,
        )
    }
}

private fun KSAnnotationWrapper.throwInvalidClassParameterException(): Nothing {
    throw EffectKspException(
        "$printableName(target = ...) has invalid parameter",
        this,
    )
}

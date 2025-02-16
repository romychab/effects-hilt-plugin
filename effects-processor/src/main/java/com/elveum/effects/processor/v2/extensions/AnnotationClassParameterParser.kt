package com.elveum.effects.processor.v2.extensions

import com.elveum.effects.processor.v2.InvalidTargetArgumentException
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType

internal fun KSAnnotationWrapper.findClassDeclaration(argumentName: String): KSClassDeclaration {
    val argument = arguments.first { it.name?.asString() == argumentName }
    val argumentValue = argument.value as? KSType
        ?: throw InvalidTargetArgumentException(this)
    return argumentValue.declaration as? KSClassDeclaration
        ?: throw InvalidTargetArgumentException(this)
}


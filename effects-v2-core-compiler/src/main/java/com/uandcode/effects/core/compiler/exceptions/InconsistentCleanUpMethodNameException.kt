package com.uandcode.effects.core.compiler.exceptions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.uandcode.effects.core.compiler.api.AbstractEffectKspException

internal class InconsistentCleanUpMethodNameException(
    interfaceDeclaration: KSClassDeclaration,
    effectImplementation1: ClassName,
    cleanUpMethodName1: String,
    effectImplementation2: ClassName,
    cleanUpMethodName2: String,
) : AbstractEffectKspException(
    message = "Interface '${interfaceDeclaration.simpleName.asString()}' " +
            "has implementations with different 'cleanUpMethodName' annotation args: " +
            "1) ${effectImplementation1.simpleName}: cleanUpMethodName=${cleanUpMethodName1}; " +
            "2) ${effectImplementation2.simpleName}: cleanUpMethodName=${cleanUpMethodName2}.",
    node = interfaceDeclaration,
)

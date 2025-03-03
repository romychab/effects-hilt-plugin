package com.elveum.effects.processor.v2.exceptions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName

class InconsistentCleanUpMethodNameException(
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

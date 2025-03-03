package com.elveum.effects.processor.exceptions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName

class InconsistentHiltComponentsException(
    interfaceDeclaration: KSClassDeclaration,
    effectImplementation1: ClassName,
    hiltComponent1: ClassName,
    effectImplementation2: ClassName,
    hiltComponent2: ClassName,
) : AbstractEffectKspException(
    message = "Interface '${interfaceDeclaration.simpleName.asString()}' " +
            "has implementations installed in different components: " +
            "1) ${effectImplementation1.simpleName} is installed in ${hiltComponent1.simpleName}; " +
            "2) ${effectImplementation2.simpleName} is installed in ${hiltComponent2.simpleName}.",
    node = interfaceDeclaration,
)

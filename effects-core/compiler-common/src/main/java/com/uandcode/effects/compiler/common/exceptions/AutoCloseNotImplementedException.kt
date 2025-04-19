package com.uandcode.effects.compiler.common.exceptions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.uandcode.effects.compiler.common.api.AbstractEffectKspException

internal class AutoCloseNotImplementedException(
    interfaceDeclaration: KSClassDeclaration,
) : AbstractEffectKspException(
    message = "The interface '${interfaceDeclaration.simpleName.asString()}' or its superinterface implements AutoCloseable, " +
            "but does not provide default implementation of close() method. Please, add a default implementation to the interface:\n" +
            "interface ${interfaceDeclaration.simpleName.asString()} : AutoCloseable {\n" +
            "    override fun close() = Unit\n" +
            "}",
    node = interfaceDeclaration
)

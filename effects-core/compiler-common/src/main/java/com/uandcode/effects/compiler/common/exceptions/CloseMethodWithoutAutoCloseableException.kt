package com.uandcode.effects.compiler.common.exceptions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.uandcode.effects.compiler.common.api.AbstractEffectKspException

internal class CloseMethodWithoutAutoCloseableException(
    interfaceDeclaration: KSClassDeclaration,
) : AbstractEffectKspException(
    message = "Target interface '${interfaceDeclaration.simpleName.asString()}' (or its superinterface) contains close() method, " +
            "but it does not implement kotlin.AutoCloseable. " +
            "Please, implement AutoCloseable and use default implementation for close() method:\n" +
            "interface ${interfaceDeclaration.simpleName.asString()} : AutoCloseable {\n" +
            "    override fun close() = Unit\n" +
            "}",
    node = interfaceDeclaration,
)

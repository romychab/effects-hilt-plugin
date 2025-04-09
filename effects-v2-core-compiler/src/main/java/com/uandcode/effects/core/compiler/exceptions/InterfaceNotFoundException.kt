package com.uandcode.effects.core.compiler.exceptions

import com.google.devtools.ksp.symbol.KSNode
import com.uandcode.effects.core.compiler.api.AbstractEffectKspException

internal class InterfaceNotFoundException(
    interfaceName: String,
    effectAnnotation: String,
    node: KSNode,
) : AbstractEffectKspException(
    "Interface '$interfaceName' not found, but used in @$effectAnnotation. " +
    "Please make sure that the interface: " +
    "1) is in the classpath and visible from the application module " +
    "2) has public modifier",
    node,
)

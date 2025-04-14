package com.uandcode.effects.koin.compiler.exception

import com.google.devtools.ksp.symbol.KSNode
import com.uandcode.effects.compiler.common.api.AbstractEffectKspException

class ScopeAnnotationConflictException(
    node: KSNode,
) : AbstractEffectKspException(
    "Both @InstallEffectToClassScope and @InstallEffectToNamedScope annotations are " +
            "not allowed for the same class. Please, use only one of them.",
    node,
)

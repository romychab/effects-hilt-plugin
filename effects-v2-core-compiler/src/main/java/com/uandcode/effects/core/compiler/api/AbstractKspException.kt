package com.uandcode.effects.core.compiler.api

import com.google.devtools.ksp.symbol.KSNode

/**
 * Base class for all exceptions thrown by the KSP processor.
 */
public abstract class AbstractEffectKspException(
    message: String,
    internal val node: KSNode,
) : Exception(message)

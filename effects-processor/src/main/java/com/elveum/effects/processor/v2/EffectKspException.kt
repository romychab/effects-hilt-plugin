package com.elveum.effects.processor.v2

import com.google.devtools.ksp.symbol.KSNode

class EffectKspException(
    message: String,
    val node: KSNode,
) : Exception(message)

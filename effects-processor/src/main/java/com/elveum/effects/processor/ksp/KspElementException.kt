package com.elveum.effects.processor.ksp

import com.google.devtools.ksp.symbol.KSNode

class KspElementException(
    message: String,
    val element: KSNode,
) : Exception(message)

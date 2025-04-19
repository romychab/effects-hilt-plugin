package com.uandcode.effects.core.testing.ksp

import org.intellij.lang.annotations.Language

public data class OutputFile(
    @Language("kotlin")
    val content: String,
    val name: String,
)

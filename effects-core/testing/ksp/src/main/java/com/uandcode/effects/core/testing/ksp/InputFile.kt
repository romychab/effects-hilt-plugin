package com.uandcode.effects.core.testing.ksp

import org.intellij.lang.annotations.Language

public data class InputFile(
    @Language("kotlin") val content: String,
    val name: String = "Input.kt",
)

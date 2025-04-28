package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class AbstractClassTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
       
        import com.uandcode.effects.hilt.annotations.HiltEffect

        interface Effect

        @HiltEffect
        abstract class EffectImpl : Effect

    """.trimIndent()

    @Test
    fun `compilation of annotated abstract class should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:6: Class annotated with @HiltEffect should not be abstract")
    }

}
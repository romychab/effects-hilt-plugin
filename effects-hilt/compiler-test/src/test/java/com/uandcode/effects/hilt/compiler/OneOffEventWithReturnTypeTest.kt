package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class OneOffEventWithReturnTypeTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect
        
        interface Effect {
            fun invalidOneOffEvent(input: String): String
        }

        @HiltEffect
        class EffectImpl : Effect {
            override fun invalidOneOffEvent(input: String) = ""
        }

    """.trimIndent()

    @Test
    fun `compilation of one-off event with return type should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:6: Non-suspend methods can't have a return type")
    }

}

package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class OneOffEventWithReturnTypeTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.core.annotations.EffectClass
        
        interface Effect {
            fun invalidOneOffEvent(input: String): String
        }

        @EffectClass
        class EffectImpl : Effect {
            override fun invalidOneOffEvent(input: String) = ""
        }

    """.trimIndent()

    @Test
    fun `compilation of one-off event with return type should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:6: Non-suspend methods can't have a return type")
    }

}

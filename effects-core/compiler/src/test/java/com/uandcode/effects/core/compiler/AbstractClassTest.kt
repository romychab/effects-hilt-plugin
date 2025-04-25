package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class AbstractClassTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
       
        import com.uandcode.effects.core.annotations.EffectClass

        interface Effect

        @EffectClass
        abstract class EffectImpl : Effect

    """.trimIndent()

    @Test
    fun `compilation of annotated abstract class should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:6: Class annotated with @EffectClass should not be abstract")
    }

}
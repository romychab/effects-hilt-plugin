package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class ClassNotImplementingInterfaceTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
       
        import com.uandcode.effects.core.annotations.EffectClass

        @EffectClass
        class EffectImpl 

    """.trimIndent()

    @Test
    fun `compilation of annotated class not implementing interface should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:4: Class annotated with @EffectClass should implement at least one interface")
    }

}
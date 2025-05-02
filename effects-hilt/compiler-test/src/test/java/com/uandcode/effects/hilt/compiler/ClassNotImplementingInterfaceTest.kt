package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class ClassNotImplementingInterfaceTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
       
        import com.uandcode.effects.hilt.annotations.HiltEffect

        @HiltEffect
        class EffectImpl 

    """.trimIndent()

    @Test
    fun `compilation of annotated class not implementing interface should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:4: Class annotated with @HiltEffect should implement at least one interface")
    }

}
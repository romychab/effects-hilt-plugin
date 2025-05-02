package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class InterfaceTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
       
        import com.uandcode.effects.hilt.annotations.HiltEffect

        interface Effect

        @HiltEffect
        interface ChildEffect : Effect

    """.trimIndent()

    @Test
    fun `compilation of annotated interface should fail`() = with(compile(source)){
        assertCompileError("Input.kt:6: Symbol annotated with @HiltEffect should be a Class or Object")
    }

}
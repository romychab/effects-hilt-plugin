package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class InterfaceTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
       
        import com.uandcode.effects.core.annotations.EffectClass

        interface Effect

        @EffectClass
        interface ChildEffect : Effect

    """.trimIndent()

    @Test
    fun `compilation of annotated interface should fail`() = with(compile(source)){
        assertCompileError("Input.kt:6: Symbol annotated with @EffectClass should be a Class or Object")
    }

}
package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class TargetInterfaceWithTypeParameterTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
       
        import com.uandcode.effects.hilt.annotations.HiltEffect

        interface Effect<T>

        @HiltEffect
        class EffectImpl<T> : Effect<T>

    """.trimIndent()

    @Test
    fun `compilation of effect implementing interface with type parameter should fail`() = with(compile(source)){
        assertCompileError("Input.kt:6: Class annotated with @HiltEffect should not have type parameters")
    }

}
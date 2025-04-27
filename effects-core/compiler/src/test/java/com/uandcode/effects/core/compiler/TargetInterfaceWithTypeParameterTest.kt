package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class TargetInterfaceWithTypeParameterTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
       
        import com.uandcode.effects.core.annotations.EffectClass

        interface Effect<T>

        @EffectClass
        class EffectImpl<T> : Effect<T>

    """.trimIndent()

    @Test
    fun `compilation of effect implementing interface with type parameter should fail`() = with(compile(source)){
        assertCompileError("Input.kt:6: Class annotated with @EffectClass should not have type parameters")
    }

}
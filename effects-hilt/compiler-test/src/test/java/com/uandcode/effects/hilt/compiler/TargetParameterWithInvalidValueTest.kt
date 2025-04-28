package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class TargetParameterWithInvalidValueTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect
        
        interface Effect1 {
            fun oneOffEvent1(input1: String)
        }

        interface Effect2 {
            fun oneOffEvent2(input2: String)
        }

        interface Effect3 {
            fun oneOffEvent3(input3: String)
        }

        @HiltEffect(targets = [Effect3::class])
        class EffectImpl : Effect1, Effect2 {
            override fun oneOffEvent1(input1: String) = Unit
            override fun oneOffEvent3(input3: String) = Unit
        }

    """.trimIndent()

    @Test
    fun `compilation of effect with 'targets' arg pointing to interface that is not implemented by the class should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:18: @HiltEffect(targets = ...) parameter can be set only to these values: Effect1::class, Effect2::class")
    }

}

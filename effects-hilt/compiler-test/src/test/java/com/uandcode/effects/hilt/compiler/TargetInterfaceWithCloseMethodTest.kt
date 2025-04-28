package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class TargetInterfaceWithCloseMethodTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.emptyFlow
        
        interface Effect {
            fun oneOffEvent(input: String)
            fun close()
        }

        @HiltEffect
        class EffectImpl : Effect {
            override fun oneOffEvent(input: String) = Unit
            override fun close() = Unit
        }

    """.trimIndent()

    @Test
    fun `compilation of effect implementing interface with close() and without AutoCloseable method should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:7: Target interface 'Effect' (or its superinterface) contains close() method, but it does not implement kotlin.AutoCloseable. Please, implement AutoCloseable and use default implementation for close() method")
    }

}

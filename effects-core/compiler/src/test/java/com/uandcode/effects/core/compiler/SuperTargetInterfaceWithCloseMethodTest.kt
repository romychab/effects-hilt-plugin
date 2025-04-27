package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class SuperTargetInterfaceWithCloseMethodTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.core.annotations.EffectClass
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.emptyFlow
        
        interface SuperEffect {
            fun superOneOffEvent(input: Int)
            fun close()
        }

        interface Effect : SuperEffect {
            fun oneOffEvent(input: String)
        }

        @EffectClass
        class EffectImpl : Effect {
            override fun superOneOffEvent(input: Int) = Unit
            override fun oneOffEvent(input: String) = Unit
            override fun close() = Unit
        }

    """.trimIndent()

    @Test
    fun `compilation of effect implementing hierarchy of 2 interfaces with close() method but without AutoCloseable should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:12: Target interface 'Effect' (or its superinterface) contains close() method, but it does not implement kotlin.AutoCloseable. Please, implement AutoCloseable and use default implementation for close() method")
    }

}

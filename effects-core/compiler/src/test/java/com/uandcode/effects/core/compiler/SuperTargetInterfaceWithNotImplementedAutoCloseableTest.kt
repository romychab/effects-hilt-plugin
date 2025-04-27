package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class SuperTargetInterfaceWithNotImplementedAutoCloseableTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.core.annotations.EffectClass
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.emptyFlow
        
        interface SuperEffect : AutoCloseable {
            fun superOneOffEvent(input: Int)
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
    fun `compilation of effect implementing hierarchy of 2 interfaces with AutoCloseable and without close() method should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:11: The interface 'Effect' or its superinterface implements AutoCloseable, but does not provide default implementation of close() method. Please, add a default implementation to the interface")
    }

}

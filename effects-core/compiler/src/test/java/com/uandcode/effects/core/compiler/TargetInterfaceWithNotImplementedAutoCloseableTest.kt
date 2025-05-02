package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class TargetInterfaceWithNotImplementedAutoCloseableTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.core.annotations.EffectClass
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.emptyFlow
        
        interface Effect : AutoCloseable {
            fun oneOffEvent(input: String)
        }

        @EffectClass
        class EffectImpl : Effect {
            override fun oneOffEvent(input: String) = Unit
            override fun close() = Unit
        }

    """.trimIndent()

    @Test
    fun `compilation of effect implementing interface with AutoCloseable and without default close() method should fail`() = with(compile(source)) {
        assertCompileError("Input.kt:7: The interface 'Effect' or its superinterface implements AutoCloseable, but does not provide default implementation of close() method. Please, add a default implementation to the interface")
    }

}

package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class TargetInterfaceWithDefaultMethodTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.core.annotations.EffectClass
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.emptyFlow
        
        interface Effect  {
            fun oneOffEvent(input: String)
            fun defaultMethod() = Unit
        }

        @EffectClass
        class EffectImpl : Effect {
            override fun oneOffEvent(input: String) = Unit
        }

    """.trimIndent()

    @Language("kotlin")
    private val expectedProxy = """
        package test

        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable
        import kotlin.String

        public class __EffectProxy(
          private val commandExecutor: CommandExecutor<Effect>,
        ) : EffectProxyMarker, Effect, AutoCloseable {
          public override fun oneOffEvent(input: String) {
             commandExecutor.execute { it.oneOffEvent(input) }
          }

          public override fun close() {
            commandExecutor.cleanUp()
          }
        }

    """.trimIndent()

    @Test
    fun `compilation of effect implementing interface with default method should complete and ignore that default method`() = with(compile(source)) {
        assertCompiled()
        assertGeneratedFileCount(2)
        assertGeneratedFile(defaultProxyName, expectedProxy)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }

}

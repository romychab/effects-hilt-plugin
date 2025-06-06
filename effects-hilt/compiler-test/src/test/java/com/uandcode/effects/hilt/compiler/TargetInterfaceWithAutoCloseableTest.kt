package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class TargetInterfaceWithAutoCloseableTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.emptyFlow
        
        interface Effect : AutoCloseable {
            fun oneOffEvent(input: String)
            override fun close() = Unit
        }

        @HiltEffect
        class EffectImpl : Effect {
            override fun oneOffEvent(input: String) = Unit
        }

    """.trimIndent()

    @Language("kotlin")
    private val expectedProxy = """
        package test

        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.String

        public class __EffectProxy(
          private val commandExecutor: CommandExecutor<Effect>,
        ) : EffectProxyMarker, Effect {
          public override fun oneOffEvent(input: String) {
             commandExecutor.execute {
                it.oneOffEvent(input)
             }
          }

          public override fun close() {
            commandExecutor.cleanUp()
          }
        }
    """.trimIndent()

    @Test
    fun `compilation of effect implementing interface with AutoCloseable and default close() method should complete`() = with(compile(source)) {
        assertCompiled()
        assertGeneratedFileCount(4)
        assertGeneratedFile(defaultProxyName, expectedProxy)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
        assertGeneratedFile(defaultEffectModuleName, expectedEffectModule)
        assertGeneratedFile(defaultEffectImplModuleName, expectedEffectImplModule)
    }

}

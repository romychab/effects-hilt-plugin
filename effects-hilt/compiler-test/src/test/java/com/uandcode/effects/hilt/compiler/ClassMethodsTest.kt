package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class ClassMethodsTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.emptyFlow
        
        interface Effect {
            fun oneOffEvent(input: String)
            suspend fun suspendCall(input: String): Int
            fun flowCall(input: String): Flow<Double>
        }

        @HiltEffect
        class EffectImpl : Effect {
            override fun oneOffEvent(input: String) = Unit
            override suspend fun suspendCall(input: String): Int = 0
            override fun flowCall(input: String): Flow<Double> = emptyFlow()
        }

    """.trimIndent()

    @Language("kotlin")
    private val expectedProxy = """
        package test

        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable
        import kotlin.Double
        import kotlin.Int
        import kotlin.String
        import kotlinx.coroutines.flow.Flow

        public class __EffectProxy(
          private val commandExecutor: CommandExecutor<Effect>,
        ) : EffectProxyMarker, Effect, AutoCloseable {
          public override fun oneOffEvent(input: String) {
             commandExecutor.execute {
                it.oneOffEvent(input)
             }
          }

          public override suspend fun suspendCall(input: String): Int {
             return commandExecutor.executeCoroutine {
                it.suspendCall(input)
             }
          }

          public override fun flowCall(input: String): Flow<Double> {
             return commandExecutor.executeFlow {
                it.flowCall(input)
             }
          }

          public override fun close() {
            commandExecutor.cleanUp()
          }
        }

    """.trimIndent()

    @Test
    fun `compilation of valid three types of effects should complete`() = with(compile(source)) {
        assertCompiled()
        assertGeneratedFileCount(4)
        assertGeneratedFile(defaultProxyName, expectedProxy)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
        assertGeneratedFile(defaultEffectModuleName, expectedEffectModule)
        assertGeneratedFile(defaultEffectImplModuleName, expectedEffectImplModule)
    }

}
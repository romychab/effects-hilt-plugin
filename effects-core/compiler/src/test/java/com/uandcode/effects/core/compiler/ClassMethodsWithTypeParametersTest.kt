package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class ClassMethodsWithTypeParametersTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.core.annotations.EffectClass
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.emptyFlow
        
        interface Effect {
            fun <T> oneOffEvent(input: T)
            suspend fun <I, O> suspendCall(input: I): O
            fun <I, O> flowCall(input: I): Flow<O>
        }

        @EffectClass
        class EffectImpl : Effect {
            override fun <T> oneOffEvent(input: T) = Unit
            override suspend fun <I, O> suspendCall(input: I): O {
                throw Exception("Not implemented")
            }
            override fun <I, O> flowCall(input: I): Flow<O> = emptyFlow()
        }

    """.trimIndent()

    @Language("kotlin")
    private val expectedProxy = """
        package test

        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable
        import kotlinx.coroutines.flow.Flow

        public class __EffectProxy(
          private val commandExecutor: CommandExecutor<Effect>,
        ) : EffectProxyMarker, Effect, AutoCloseable {
          public override fun <T> oneOffEvent(input: T) {
             commandExecutor.execute {
                it.oneOffEvent(input)
             }
          }

          public override suspend fun <I, O> suspendCall(input: I): O {
             return commandExecutor.executeCoroutine {
                it.suspendCall(input)
             }
          }

          public override fun <I, O> flowCall(input: I): Flow<O> {
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
    fun `compilation of valid three types of effects with type parameters should complete`() = with(compile(source)) {
        assertCompiled()
        assertGeneratedFileCount(2)
        assertGeneratedFile(defaultProxyName, expectedProxy)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }

}
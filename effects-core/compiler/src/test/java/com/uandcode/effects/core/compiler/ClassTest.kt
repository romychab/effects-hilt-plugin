package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class ClassTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.core.annotations.EffectClass
        
        interface Effect

        @EffectClass
        class EffectImpl : Effect

    """.trimIndent()

    @Language("kotlin")
    private val expectedProxy = """
        package test
        
        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable

        public class __EffectProxy(
          private val commandExecutor: CommandExecutor<Effect>,
        ) : EffectProxyMarker, Effect, AutoCloseable {
          public override fun close() {
            commandExecutor.cleanUp()
          }
        }
    """.trimIndent()

    @Test
    fun `compilation of valid simple annotated class with one interface should complete`() = with(compile(source)) {
        assertCompiled()
        assertGeneratedFileCount(2)
        assertGeneratedFile(defaultProxyName, expectedProxy)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }

}

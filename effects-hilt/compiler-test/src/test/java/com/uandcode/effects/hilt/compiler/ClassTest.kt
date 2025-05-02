package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class ClassTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect
        
        interface Effect

        @HiltEffect
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
        assertGeneratedFileCount(4)
        assertGeneratedFile(defaultProxyName, expectedProxy)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
        assertGeneratedFile(defaultEffectModuleName, expectedEffectModule)
        assertGeneratedFile(defaultEffectImplModuleName, expectedEffectImplModule)
    }

}

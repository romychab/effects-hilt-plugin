package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class SubclassWithInterfaceTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect

        interface Effect {
            fun execute(input: String)
        }

        abstract class OtherClass {
            abstract fun otherFun()
        }

        @HiltEffect
        class EffectImpl : OtherClass(), Effect {
            override fun execute(input: String) = Unit
            override fun otherFun() = Unit
        }

    """.trimIndent()

    @Language("kotlin")
    val expectedProxy = """
        package test

        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable
        import kotlin.String

        public class __EffectProxy(
          private val commandExecutor: CommandExecutor<Effect>,
        ) : EffectProxyMarker, Effect, AutoCloseable {
        
          public override fun execute(input: String) {
             commandExecutor.execute {
                it.execute(input)
             }
          }

          public override fun close() {
            commandExecutor.cleanUp()
          }
        }
    """.trimIndent()

    @Test
    fun `compilation of class implementing target interface and other superclass should complete`() = with(compile(source)){
        assertCompiled()
        assertGeneratedFileCount(4)
        assertGeneratedFile(defaultProxyName, expectedProxy)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
        assertGeneratedFile(defaultEffectModuleName, expectedEffectModule)
        assertGeneratedFile(defaultEffectImplModuleName, expectedEffectImplModule)
    }

}
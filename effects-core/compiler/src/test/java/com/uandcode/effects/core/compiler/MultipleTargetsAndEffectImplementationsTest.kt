package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class MultipleTargetsAndEffectImplementationsTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.core.annotations.EffectClass
        
        interface Effect1 {
            fun oneOffEvent1(input1: String)
        }

        interface Effect2 {
            fun oneOffEvent2(input2: String)
        }

        interface Effect3 {
            fun oneOffEvent3(input3: String)
        }


        @EffectClass
        class LeftEffectImpl : Effect1, Effect2 {
            override fun oneOffEvent1(input1: String) = Unit
            override fun oneOffEvent2(input2: String) = Unit
        }

        @EffectClass
        class RightEffectImpl : Effect2, Effect3 {
            override fun oneOffEvent2(input2: String) = Unit
            override fun oneOffEvent3(input3: String) = Unit
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedProxy1 = """
        package test
        
        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable
        import kotlin.String
        
        public class __Effect1Proxy(
          private val commandExecutor: CommandExecutor<Effect1>,
        ) : EffectProxyMarker, Effect1, AutoCloseable {
          public override fun oneOffEvent1(input1: String) {
             commandExecutor.execute { it.oneOffEvent1(input1) }
          }
          public override fun close() { commandExecutor.cleanUp() }
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedProxy2 = """
        package test
        
        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable
        import kotlin.String
        
        public class __Effect2Proxy(
          private val commandExecutor: CommandExecutor<Effect2>,
        ) : EffectProxyMarker, Effect2, AutoCloseable {
          public override fun oneOffEvent2(input2: String) {
             commandExecutor.execute { it.oneOffEvent2(input2) }
          }
          public override fun close() { commandExecutor.cleanUp() }
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedProxy3 = """
        package test
        
        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable
        import kotlin.String
        
        public class __Effect3Proxy(
          private val commandExecutor: CommandExecutor<Effect3>,
        ) : EffectProxyMarker, Effect3, AutoCloseable {
          public override fun oneOffEvent3(input3: String) {
             commandExecutor.execute { it.oneOffEvent3(input3) }
          }
          public override fun close() { commandExecutor.cleanUp() }
        }
    """.trimIndent()

    @Language("kotlin")
    override val expectedEffectStore = """
        package com.uandcode.effects.core.kspcontract

        import com.uandcode.effects.core.`internal`.InternalProxyEffectStoreImpl
        import com.uandcode.effects.stub.api.ProxyConfiguration
        import com.uandcode.effects.stub.api.ProxyEffectStore
        import test.Effect1
        import test.Effect2
        import test.Effect3
        import test.LeftEffectImpl
        import test.RightEffectImpl
        import test.__Effect1Proxy
        import test.__Effect2Proxy
        import test.__Effect3Proxy

        private val instance: InternalProxyEffectStoreImpl =
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@EffectClass")).apply {
              registerProxyProvider(Effect1::class, ::__Effect1Proxy)
              registerProxyProvider(Effect2::class, ::__Effect2Proxy)
              registerProxyProvider(Effect3::class, ::__Effect3Proxy)
              registerTarget(LeftEffectImpl::class, Effect1::class)
              registerTarget(LeftEffectImpl::class, Effect2::class)
              registerTarget(RightEffectImpl::class, Effect2::class)
              registerTarget(RightEffectImpl::class, Effect3::class)
            }

        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance

    """.trimIndent()

    @Test
    fun `compilation of multiple effects implementing multiple target interfaces should complete`() = with(compile(source)) {
        assertCompiled()
        assertGeneratedFileCount(4)
        assertGeneratedFile("test/__Effect1Proxy.kt", expectedProxy1)
        assertGeneratedFile("test/__Effect2Proxy.kt", expectedProxy2)
        assertGeneratedFile("test/__Effect3Proxy.kt", expectedProxy3)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }

}

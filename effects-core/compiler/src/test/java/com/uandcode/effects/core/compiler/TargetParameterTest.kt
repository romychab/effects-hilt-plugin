package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class TargetParameterTest : AbstractCoreKspTest() {

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

        interface IgnoredEffect {
            fun oneOffEvent3(input3: String)
        }


        @EffectClass(
            targets = [Effect1::class, Effect2::class]
        )
        class EffectImpl : Effect1, Effect2, IgnoredEffect {
            override fun oneOffEvent1(input1: String) = Unit
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

          public override fun close() {
            commandExecutor.cleanUp()
          }
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

          public override fun close() {
            commandExecutor.cleanUp()
          }
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
        import test.EffectImpl
        import test.__Effect1Proxy
        import test.__Effect2Proxy

        private val instance: InternalProxyEffectStoreImpl =
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@EffectClass")).apply {
              registerProxyProvider(Effect1::class, ::__Effect1Proxy)
              registerProxyProvider(Effect2::class, ::__Effect2Proxy)
              registerTarget(EffectImpl::class, Effect1::class)
              registerTarget(EffectImpl::class, Effect2::class)
            }

        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance
    """.trimIndent()

    @Test
    fun `compilation of effect with 'targets' arg should complete and generate proxies for interfaces listed in 'targets' arg`() = with(compile(source)) {
        assertCompiled()
        assertGeneratedFileCount(3)
        assertGeneratedFile("test/__Effect1Proxy.kt", expectedProxy1)
        assertGeneratedFile("test/__Effect2Proxy.kt", expectedProxy2)
        assertGeneratedFileDoesNotExist("test/__IgnoredEffectProxy.kt")
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }

}

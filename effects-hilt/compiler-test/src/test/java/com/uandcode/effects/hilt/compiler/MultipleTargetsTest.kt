package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class MultipleTargetsTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect
        
        interface Effect1 {
            fun oneOffEvent1(input1: String)
        }

        interface Effect2 {
            fun oneOffEvent2(input2: String)
        }

        @HiltEffect
        class EffectImpl : Effect1, Effect2 {
            override fun oneOffEvent1(input1: String) = Unit
            override fun oneOffEvent2(input2: String) = Unit
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
    private val expectedModule1 = """
        package test

        import com.uandcode.effects.core.EffectScope
        import com.uandcode.effects.core.EffectController
        import com.uandcode.effects.core.getController
        import com.uandcode.effects.core.getProxy
        import com.uandcode.effects.hilt.internal.InternalRegisteredEffect
        import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.multibindings.IntoSet
        import dagger.hilt.android.components.ActivityRetainedComponent

        @Module
        @InstallIn(ActivityRetainedComponent::class)
        internal object Effect1Module {

            @Provides
            @IntoSet
            fun registerEffect(): InternalRegisteredEffect {
                return InternalRegisteredEffect(Effect1::class, AbstractInternalQualifier.activityRetained)
            }

            @Provides
            fun provideEffect(effectScope: EffectScope): Effect1 {
                return effectScope.getProxy()
            }

            @Provides
            fun provideController(effectScope: EffectScope): EffectController<Effect1> {
                return effectScope.getController()
            }
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedModule2 = """
        package test

        import com.uandcode.effects.core.EffectScope
        import com.uandcode.effects.core.EffectController
        import com.uandcode.effects.core.getController
        import com.uandcode.effects.core.getProxy
        import com.uandcode.effects.hilt.internal.InternalRegisteredEffect
        import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.multibindings.IntoSet
        import dagger.hilt.android.components.ActivityRetainedComponent

        @Module
        @InstallIn(ActivityRetainedComponent::class)
        internal object Effect2Module {
            @Provides
            @IntoSet
            fun registerEffect(): InternalRegisteredEffect {
                return InternalRegisteredEffect(Effect2::class, AbstractInternalQualifier.activityRetained)
            }

            @Provides
            fun provideEffect(effectScope: EffectScope): Effect2 {
                return effectScope.getProxy()
            }

            @Provides
            fun provideController(effectScope: EffectScope): EffectController<Effect2> {
                return effectScope.getController()
            }
        }
    """.trimIndent()

    @Language("kotlin")
    override val expectedEffectImplModule: String = """
        package test

        import com.uandcode.effects.core.EffectScope
        import com.uandcode.effects.core.EffectController
        import com.uandcode.effects.core.getController
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.hilt.android.components.ActivityRetainedComponent

        @Module
        @InstallIn(ActivityRetainedComponent::class)
        internal object EffectImplModule {
            @Provides
            fun provideController(effectScope: EffectScope): EffectController<EffectImpl> {
                return effectScope.getController()
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
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@HiltEffect")).apply {
              registerProxyProvider(Effect1::class, ::__Effect1Proxy)
              registerProxyProvider(Effect2::class, ::__Effect2Proxy)
              registerTarget(EffectImpl::class, Effect1::class)
              registerTarget(EffectImpl::class, Effect2::class)
            }

        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance
    """.trimIndent()

    @Test
    fun `compilation of effect implementing multiple target interfaces should complete`() = with(compile(source)) {
        assertCompiled()
        assertGeneratedFileCount(6)
        assertGeneratedFile("test/__Effect1Proxy.kt", expectedProxy1)
        assertGeneratedFile("test/Effect1Module.kt", expectedModule1)
        assertGeneratedFile("test/__Effect2Proxy.kt", expectedProxy2)
        assertGeneratedFile("test/Effect2Module.kt", expectedModule2)
        assertGeneratedFile("test/EffectImplModule.kt", expectedEffectImplModule)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }

}

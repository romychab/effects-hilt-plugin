package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class MultipleTargetsAndEffectImplementationsTest : AbstractHiltKspTest() {

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

        interface Effect3 {
            fun oneOffEvent3(input3: String)
        }

        @HiltEffect
        class LeftEffectImpl : Effect1, Effect2 {
            override fun oneOffEvent1(input1: String) = Unit
            override fun oneOffEvent2(input2: String) = Unit
        }

        @HiltEffect
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
    private val expectedEffect1Module = """
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
    private val expectedEffect2Module = """
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
    private val expectedEffect3Module = """
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
        internal object Effect3Module {
            @Provides
            @IntoSet
            fun registerEffect(): InternalRegisteredEffect {
                return InternalRegisteredEffect(Effect3::class, AbstractInternalQualifier.activityRetained)
            }

            @Provides
            fun provideEffect(effectScope: EffectScope): Effect3 {
                return effectScope.getProxy()
            }

            @Provides
            fun provideController(effectScope: EffectScope): EffectController<Effect3> {
                return effectScope.getController()
            }
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedLeftEffectImplModule = """
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
        internal object LeftEffectImplModule {
            @Provides
            fun provideController(effectScope: EffectScope): EffectController<LeftEffectImpl> {
                return effectScope.getController()
            }
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedRightEffectImplModule = """
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
        internal object RightEffectImplModule {
            @Provides
            fun provideController(effectScope: EffectScope): EffectController<RightEffectImpl> {
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
        import test.Effect3
        import test.LeftEffectImpl
        import test.RightEffectImpl
        import test.__Effect1Proxy
        import test.__Effect2Proxy
        import test.__Effect3Proxy

        private val instance: InternalProxyEffectStoreImpl =
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@HiltEffect")).apply {
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
        assertGeneratedFileCount(9)
        assertGeneratedFile("test/__Effect1Proxy.kt", expectedProxy1)
        assertGeneratedFile("test/__Effect2Proxy.kt", expectedProxy2)
        assertGeneratedFile("test/__Effect3Proxy.kt", expectedProxy3)
        assertGeneratedFile("test/Effect1Module.kt", expectedEffect1Module)
        assertGeneratedFile("test/Effect2Module.kt", expectedEffect2Module)
        assertGeneratedFile("test/Effect3Module.kt", expectedEffect3Module)
        assertGeneratedFile("test/LeftEffectImplModule.kt", expectedLeftEffectImplModule)
        assertGeneratedFile("test/RightEffectImplModule.kt", expectedRightEffectImplModule)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }

}

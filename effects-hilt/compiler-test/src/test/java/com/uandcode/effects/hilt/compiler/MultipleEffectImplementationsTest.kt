package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class MultipleEffectImplementationsTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val source = """
        package test

        import com.uandcode.effects.hilt.annotations.HiltEffect
        
        interface Effect {
            fun oneOffEvent(input: String)
        }

        @HiltEffect
        class EffectImpl1 : Effect {
            override fun oneOffEvent(input: String) = Unit
        }

        @HiltEffect
        class EffectImpl2 : Effect {
            override fun oneOffEvent(input: String) = Unit
        }

    """.trimIndent()

    @Language("kotlin")
    private val expectedProxy = """
        package test
        
        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable
        import kotlin.String
        
        public class __EffectProxy(
          private val commandExecutor: CommandExecutor<Effect>,
        ) : EffectProxyMarker, Effect, AutoCloseable {
          public override fun oneOffEvent(input: String) {
             commandExecutor.execute { it.oneOffEvent(input) }
          }
        
          public override fun close() {
            commandExecutor.cleanUp()
          }
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedEffectInterfaceModule = """
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
        internal object EffectModule {
            @Provides
            @IntoSet
            fun registerEffect(): InternalRegisteredEffect {
                return InternalRegisteredEffect(Effect::class, AbstractInternalQualifier.activityRetained)
            }

            @Provides
            fun provideEffect(effectScope: EffectScope): Effect {
                return effectScope.getProxy()
            }

            @Provides
            fun provideController(effectScope: EffectScope): EffectController<Effect> {
                return effectScope.getController()
            }
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedEffectImpl1Module = """
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
        internal object EffectImpl1Module {
            @Provides
            fun provideController(effectScope: EffectScope): EffectController<EffectImpl1> {
                return effectScope.getController()
            }
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedEffectImpl2Module = """
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
        internal object EffectImpl2Module {
            @Provides
            fun provideController(effectScope: EffectScope): EffectController<EffectImpl2> {
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
        import test.Effect
        import test.EffectImpl1
        import test.EffectImpl2
        import test.__EffectProxy
        
        private val instance: InternalProxyEffectStoreImpl =
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@HiltEffect")).apply {
              registerProxyProvider(Effect::class, ::__EffectProxy)
              registerTarget(EffectImpl1::class, Effect::class)
              registerTarget(EffectImpl2::class, Effect::class)
            }
        
        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance

    """.trimIndent()

    @Test
    fun `compilation of 2 effects implementing the same target interface should complete`() = with(compile(source)) {
        assertCompiled()
        assertGeneratedFileCount(5)
        assertGeneratedFile("test/__EffectProxy.kt", expectedProxy)
        assertGeneratedFile("test/EffectModule.kt", expectedEffectInterfaceModule)
        assertGeneratedFile("test/EffectImpl1Module.kt", expectedEffectImpl1Module)
        assertGeneratedFile("test/EffectImpl2Module.kt", expectedEffectImpl2Module)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }

}

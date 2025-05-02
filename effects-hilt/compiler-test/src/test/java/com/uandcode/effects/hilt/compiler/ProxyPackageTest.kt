package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import com.uandcode.effects.core.testing.ksp.InputFile
import org.intellij.lang.annotations.Language
import org.junit.Test

class ProxyPackageTest : AbstractHiltKspTest() {

    private val effectInterfaceSource = InputFile(
        name = "interface_package/Effect.kt",
        content = """
            package interface_package
            interface Effect    
        """.trimIndent()
    )

    private val effectImplementationSource = InputFile(
        name = "impl_package/EffectImpl.kt",
        content = """
            package impl_package

            import com.uandcode.effects.hilt.annotations.HiltEffect
            import interface_package.Effect

            @HiltEffect
            class EffectImpl : Effect
        """.trimIndent()
    )

    @Language("kotlin")
    private val expectedProxy = """
       package interface_package

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

    @Language("kotlin")
    override val expectedEffectModule: String = """
        package interface_package

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
    override val expectedEffectImplModule: String = """
        package impl_package

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

    override val expectedEffectStore: String = """
        package com.uandcode.effects.core.kspcontract

        import com.uandcode.effects.core.`internal`.InternalProxyEffectStoreImpl
        import com.uandcode.effects.stub.api.ProxyConfiguration
        import com.uandcode.effects.stub.api.ProxyEffectStore
        import impl_package.EffectImpl
        import interface_package.Effect
        import interface_package.__EffectProxy

        private val instance: InternalProxyEffectStoreImpl =
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@HiltEffect")).apply {
              registerProxyProvider(Effect::class, ::__EffectProxy)
              registerTarget(EffectImpl::class, Effect::class)
            }

        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance
    """.trimIndent()

    @Test
    fun `generated proxy must be placed to the package of target interface`() =
        with(compile(effectInterfaceSource, effectImplementationSource)) {
            assertCompiled()
            assertGeneratedFileCount(4)
            assertGeneratedFile("interface_package/__EffectProxy.kt", expectedProxy)
            assertGeneratedFile("interface_package/EffectModule.kt", expectedEffectModule)
            assertGeneratedFile("impl_package/EffectImplModule.kt", expectedEffectImplModule)
            assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
        }

}

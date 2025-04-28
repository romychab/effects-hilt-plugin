package com.uandcode.effects.hilt.compiler.base

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.uandcode.effects.core.testing.ksp.AbstractKspTest
import com.uandcode.effects.hilt.compiler.HiltSymbolProcessorProvider
import org.intellij.lang.annotations.Language

abstract class AbstractHiltKspTest : AbstractKspTest() {

    override val symbolProcessorProvider: SymbolProcessorProvider
        get() = HiltSymbolProcessorProvider()

    val defaultProxyName = "test/__EffectProxy.kt"
    val defaultEffectModuleName = "test/EffectModule.kt"
    val defaultEffectImplModuleName = "test/EffectImplModule.kt"

    @Language("kotlin")
    open val expectedEffectImplModule: String = """
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
    open val expectedEffectModule: String = """
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
    open val expectedEffectStore: String = """
        package com.uandcode.effects.core.kspcontract

        import com.uandcode.effects.core.`internal`.InternalProxyEffectStoreImpl
        import com.uandcode.effects.stub.api.ProxyConfiguration
        import com.uandcode.effects.stub.api.ProxyEffectStore
        import test.Effect
        import test.EffectImpl
        import test.__EffectProxy

        private val instance: InternalProxyEffectStoreImpl =
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@HiltEffect")).apply {
              registerProxyProvider(Effect::class, ::__EffectProxy)
              registerTarget(EffectImpl::class, Effect::class)
            }

        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance
    """.trimIndent()
}

package com.uandcode.effects.core.compiler.base

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.uandcode.effects.core.compiler.CoreSymbolProcessorProvider
import com.uandcode.effects.core.testing.ksp.AbstractKspTest
import org.intellij.lang.annotations.Language

abstract class AbstractCoreKspTest : AbstractKspTest() {

    override val symbolProcessorProvider: SymbolProcessorProvider
        get() = CoreSymbolProcessorProvider()

    val defaultProxyName = "test/__EffectProxy.kt"

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
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@EffectClass")).apply {
              registerProxyProvider(Effect::class, ::__EffectProxy)
              registerTarget(EffectImpl::class, Effect::class)
            }

        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance
    """.trimIndent()
}

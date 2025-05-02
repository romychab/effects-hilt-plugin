package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import com.uandcode.effects.core.testing.ksp.InputFile
import org.intellij.lang.annotations.Language
import org.junit.Test

class ProxyPackageTest : AbstractCoreKspTest() {

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

            import com.uandcode.effects.core.annotations.EffectClass
            import interface_package.Effect

            @EffectClass
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

    override val expectedEffectStore: String = """
        package com.uandcode.effects.core.kspcontract

        import com.uandcode.effects.core.`internal`.InternalProxyEffectStoreImpl
        import com.uandcode.effects.stub.api.ProxyConfiguration
        import com.uandcode.effects.stub.api.ProxyEffectStore
        import impl_package.EffectImpl
        import interface_package.Effect
        import interface_package.__EffectProxy

        private val instance: InternalProxyEffectStoreImpl =
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@EffectClass")).apply {
              registerProxyProvider(Effect::class, ::__EffectProxy)
              registerTarget(EffectImpl::class, Effect::class)
            }

        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance
    """.trimIndent()

    @Test
    fun `generated proxy must be placed to the package of target interface`() =
        with(compile(effectInterfaceSource, effectImplementationSource)) {
            assertCompiled()
            assertGeneratedFileCount(2)
            assertGeneratedFile("interface_package/__EffectProxy.kt", expectedProxy)
            assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
        }

}

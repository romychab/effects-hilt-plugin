package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class ObjectTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val source = """
        package object_test

        import com.uandcode.effects.core.annotations.EffectClass

        interface ObjectEffect

        @EffectClass
        object ObjectEffectImpl : ObjectEffect

    """.trimIndent()

    @Language("kotlin")
    private val expectedProxy = """
        package object_test

        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable

        public class __ObjectEffectProxy(
            private val commandExecutor: CommandExecutor<ObjectEffect>,
        ) : EffectProxyMarker, ObjectEffect, AutoCloseable {

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
        import object_test.ObjectEffect
        import object_test.ObjectEffectImpl
        import object_test.__ObjectEffectProxy

        private val instance: InternalProxyEffectStoreImpl =
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@EffectClass")).apply {
              registerProxyProvider(ObjectEffect::class, ::__ObjectEffectProxy)
              registerTarget(ObjectEffectImpl::class, ObjectEffect::class)
            }

        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance

    """.trimIndent()

    @Test
    fun `compilation of effect object should complete`() = with(compile(source)) {
        assertCompiled()
        assertGeneratedFileCount(2)
        assertGeneratedFile("object_test/__ObjectEffectProxy.kt", expectedProxy)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }

}
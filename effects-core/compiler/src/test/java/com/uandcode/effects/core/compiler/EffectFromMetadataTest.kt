package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import com.uandcode.effects.core.testing.ksp.InputFile
import org.intellij.lang.annotations.Language
import org.junit.Test

class EffectFromMetadataTest : AbstractCoreKspTest() {

    @Language("kotlin")
    private val inputMetadata = """
        package com.uandcode.effects.compiler.common.generated

        import com.uandcode.effects.core.annotations.EffectMetadata

        @EffectMetadata(
            interfaceClassNames = ["interface1_package.CompiledEffect1", "interface2_package.CompiledEffect2"],
            implementationClassName = "implementation_package.CompiledEffectImpl",
        )
        public class __implementation_package_EffectImpl_Metadata
    """.trimIndent()

    private val inputSource = InputFile(
        content = inputMetadata,
        name = "com/uandcode/effects/compiler/common/generated/__implementation_package_EffectImpl_Metadata.kt"
    )

    @Language("kotlin")
    private val expectedProxyEffect1 = """
        package interface1_package

        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable
        import kotlin.String
        
        public class __CompiledEffect1Proxy(
          private val commandExecutor: CommandExecutor<CompiledEffect1>,
        ) : EffectProxyMarker, CompiledEffect1, AutoCloseable {
          public override fun runEffect1(input1: String) {
             commandExecutor.execute { it.runEffect1(input1) }
          }
          public override fun close() { commandExecutor.cleanUp() }
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedProxyEffect2 = """
        package interface2_package

        import com.uandcode.effects.core.CommandExecutor
        import com.uandcode.effects.core.EffectProxyMarker
        import kotlin.AutoCloseable
        import kotlin.Int

        public class __CompiledEffect2Proxy(
          private val commandExecutor: CommandExecutor<CompiledEffect2>,
        ) : EffectProxyMarker, CompiledEffect2, AutoCloseable {
          public override fun runEffect2(input2: Int) {
             commandExecutor.execute { it.runEffect2(input2) }
          }
          public override fun close() { commandExecutor.cleanUp() }
        }
    """.trimIndent()

    @Language("kotlin")
    override val expectedEffectStore: String = """
        package com.uandcode.effects.core.kspcontract

        import com.uandcode.effects.core.`internal`.InternalProxyEffectStoreImpl
        import com.uandcode.effects.stub.api.ProxyConfiguration
        import com.uandcode.effects.stub.api.ProxyEffectStore
        import implementation_package.CompiledEffectImpl
        import interface1_package.CompiledEffect1
        import interface1_package.__CompiledEffect1Proxy
        import interface2_package.CompiledEffect2
        import interface2_package.__CompiledEffect2Proxy

        private val instance: InternalProxyEffectStoreImpl =
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@EffectClass")).apply {
              registerProxyProvider(CompiledEffect1::class, ::__CompiledEffect1Proxy)
              registerProxyProvider(CompiledEffect2::class, ::__CompiledEffect2Proxy)
              registerTarget(CompiledEffectImpl::class, CompiledEffect1::class)
              registerTarget(CompiledEffectImpl::class, CompiledEffect2::class)
            }

        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance
    """.trimIndent()

    @Test
    fun `generated metadata should be processed`() = with(compile(inputSource)) {
        assertCompiled()
        assertGeneratedFileCount(3)
        assertGeneratedFile("interface1_package/__CompiledEffect1Proxy.kt", expectedProxyEffect1)
        assertGeneratedFile("interface2_package/__CompiledEffect2Proxy.kt", expectedProxyEffect2)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }
}
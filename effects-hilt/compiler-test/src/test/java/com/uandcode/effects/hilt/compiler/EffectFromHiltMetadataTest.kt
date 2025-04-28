package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.core.testing.ksp.InputFile
import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Test

class EffectFromHiltMetadataTest : AbstractHiltKspTest() {

    @Language("kotlin")
    private val inputMetadata = """
        package com.uandcode.effects.compiler.common.generated

        import com.uandcode.effects.hilt.annotations.HiltEffectMetadata

        @HiltEffectMetadata(
            interfaceClassNames = ["interface1_package.CompiledEffect1", "interface2_package.CompiledEffect2"],
            implementationClassName = "implementation_package.CompiledEffectImpl",
            hiltComponentClassName = "dagger.hilt.android.components.ActivityRetainedComponent",
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
            InternalProxyEffectStoreImpl(ProxyConfiguration.Default("@HiltEffect")).apply {
              registerProxyProvider(CompiledEffect1::class, ::__CompiledEffect1Proxy)
              registerProxyProvider(CompiledEffect2::class, ::__CompiledEffect2Proxy)
              registerTarget(CompiledEffectImpl::class, CompiledEffect1::class)
              registerTarget(CompiledEffectImpl::class, CompiledEffect2::class)
            }

        public object AnnotationBasedProxyEffectStore : ProxyEffectStore by instance
    """.trimIndent()

    @Language("kotlin")
    private val expectedEffect1Module = """
        package interface1_package

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

        @Module @InstallIn(ActivityRetainedComponent::class)
        internal object CompiledEffect1Module {
            @Provides @IntoSet
            fun registerEffect(): InternalRegisteredEffect {
                return InternalRegisteredEffect(CompiledEffect1::class, AbstractInternalQualifier.activityRetained)
            }

            @Provides
            fun provideEffect(effectScope: EffectScope): CompiledEffect1 {
                return effectScope.getProxy()
            }

            @Provides
            fun provideController(effectScope: EffectScope): EffectController<CompiledEffect1> {
                return effectScope.getController()
            }
        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedEffect2Module = """
        package interface2_package

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

        @Module @InstallIn(ActivityRetainedComponent::class)
        internal object CompiledEffect2Module {
            @Provides @IntoSet
            fun registerEffect(): InternalRegisteredEffect {
                return InternalRegisteredEffect(CompiledEffect2::class, AbstractInternalQualifier.activityRetained)
            }

            @Provides
            fun provideEffect(effectScope: EffectScope): CompiledEffect2 {
                return effectScope.getProxy()
            }

            @Provides
            fun provideController(effectScope: EffectScope): EffectController<CompiledEffect2> {
                return effectScope.getController()
            }
        }
    """.trimIndent()

    @Language("kotlin")
    override val expectedEffectImplModule = """
        package implementation_package

        import com.uandcode.effects.core.EffectScope
        import com.uandcode.effects.core.EffectController
        import com.uandcode.effects.core.getController
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.hilt.android.components.ActivityRetainedComponent

        @Module @InstallIn(ActivityRetainedComponent::class)
        internal object CompiledEffectImplModule {
            @Provides
            fun provideController(effectScope: EffectScope): EffectController<CompiledEffectImpl> {
                return effectScope.getController()
            }
        }
    """.trimIndent()

    @Test
    fun `generated metadata should be processed`() = with(compile(inputSource)) {
        assertCompiled()
        assertGeneratedFileCount(6)
        assertGeneratedFile("interface1_package/__CompiledEffect1Proxy.kt", expectedProxyEffect1)
        assertGeneratedFile("interface1_package/CompiledEffect1Module.kt", expectedEffect1Module)
        assertGeneratedFile("interface2_package/__CompiledEffect2Proxy.kt", expectedProxyEffect2)
        assertGeneratedFile("interface2_package/CompiledEffect2Module.kt", expectedEffect2Module)
        assertGeneratedFile("implementation_package/CompiledEffectImplModule.kt", expectedEffectImplModule)
        assertGeneratedFile(defaultEffectStoreName, expectedEffectStore)
    }
}
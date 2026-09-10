package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.core.testing.ksp.InputFile
import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import org.intellij.lang.annotations.Language
import org.junit.Assert.assertTrue
import org.junit.Test

class CoreModulesTest : AbstractHiltKspTest() {

    private val source = InputFile(
        name = "test/Effect.kt",
        content = """
            package test

            import com.uandcode.effects.hilt.annotations.HiltEffect

            interface Effect

            @HiltEffect
            class EffectImpl : Effect
        """.trimIndent()
    )

    private val corePath = "com/uandcode/effects/hilt/generated"

    @Language("kotlin")
    private val expectedSingletonModule = """
        @file:OptIn(com.uandcode.effects.hilt.InternalEffectsHiltApi::class)

        package com.uandcode.effects.hilt.generated

        import com.uandcode.effects.core.ManagedInterfaces
        import com.uandcode.effects.core.RootEffectScopes
        import com.uandcode.effects.hilt.internal.InternalRegisteredEffect
        import com.uandcode.effects.hilt.internal.filterByQualifier
        import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
        import com.uandcode.effects.hilt.internal.qualifiers.SingletonQualifier
        import dagger.Module
        import dagger.Provides
        import dagger.hilt.InstallIn
        import dagger.hilt.components.SingletonComponent
        import dagger.multibindings.IntoSet
        import javax.inject.Singleton

        @Module
        @InstallIn(SingletonComponent::class)
        public object SingletonEffectModule {

            @Provides
            @Singleton
            public fun provideQualifier(
                registeredEffects: Set<@JvmSuppressWildcards InternalRegisteredEffect>
            ): SingletonQualifier {
                return SingletonQualifier(
                    RootEffectScopes.empty.createChild(
                        ManagedInterfaces.ListOf(
                            *registeredEffects.filterByQualifier(SingletonQualifier::class)
                        )
                    )
                )
            }

            @Provides
            @IntoSet
            public fun provideQualifierToSet(
                qualifier: SingletonQualifier
            ): AbstractInternalQualifier = qualifier

        }
    """.trimIndent()

    @Language("kotlin")
    private val expectedActivityEntryPoint = """
        package com.uandcode.effects.hilt.generated

        import com.uandcode.effects.hilt.ActivityEffectEntryPoint
        import dagger.hilt.EntryPoint
        import dagger.hilt.InstallIn
        import dagger.hilt.android.components.ActivityComponent

        @EntryPoint
        @InstallIn(ActivityComponent::class)
        public interface GeneratedActivityEffectEntryPoint : ActivityEffectEntryPoint
    """.trimIndent()

    @Test
    fun `aggregate mode generates all eight core files exactly once`() =
        with(compile(source)) {
            assertCompiled()
            assertGeneratedFile("$corePath/SingletonEffectModule.kt", expectedSingletonModule)
            assertGeneratedFile("$corePath/GeneratedActivityEffectEntryPoint.kt", expectedActivityEntryPoint)
            listOf(
                "ActivityRetainedEffectModule",
                "ViewModelEffectModule",
                "ActivityEffectModule",
                "FragmentEffectModule",
                "MultibindingModule",
                "GeneratedFragmentEffectEntryPoint",
            ).forEach { name ->
                assertTrue(
                    "Expected generated file '$name'",
                    generatedKspFiles.any { it.name == "$corePath/$name.kt" },
                )
            }
            // 8 core files + proxy + effect module + impl module + effect store
            assertGeneratedFileCount(8 + 4)
        }

    @Test
    fun `generate mode emits no core files`() =
        with(GenerateModeTest().compileInGenerateMode(source)) {
            assertCompiled()
            assertGeneratedFileDoesNotExist("$corePath/SingletonEffectModule.kt")
            assertGeneratedFileDoesNotExist("$corePath/MultibindingModule.kt")
            assertGeneratedFileDoesNotExist("$corePath/GeneratedActivityEffectEntryPoint.kt")
        }

    private class GenerateModeTest : AbstractHiltKspTest() {
        override val options: Map<String, String> =
            mapOf("effects.processor.metadata" to "generate")

        fun compileInGenerateMode(vararg sources: InputFile) = compile(*sources)
    }
}

package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import com.uandcode.effects.core.testing.ksp.InputFile
import org.intellij.lang.annotations.Language
import org.junit.Test

class MetadataTest : AbstractHiltKspTest() {

    override val options: Map<String, String> = mutableMapOf(
        "effects.processor.metadata" to "generate"
    )

    private val interfaceSource = InputFile(
        name = "interface_package/Effect.kt",
        content = """
            package interface_package
            import com.uandcode.effects.hilt.annotations.HiltEffect
            interface Effect
        """.trimIndent()
    )

    private val implementationSource = InputFile(
        name = "implementation_package/EffectImpl.kt",
        content = """
            package implementation_package
    
            import com.uandcode.effects.hilt.annotations.HiltEffect
            import interface_package.Effect
    
            @HiltEffect
            class EffectImpl : Effect
        """.trimIndent()
    )

    @Language("kotlin")
    private val expectedOutputMetadata = """
        package com.uandcode.effects.compiler.common.generated

        import com.uandcode.effects.hilt.annotations.HiltEffectMetadata

        @HiltEffectMetadata(
            interfaceClassNames = ["interface_package.Effect"],
            implementationClassName = "implementation_package.EffectImpl",
            hiltComponentClassName = "dagger.hilt.android.components.ActivityRetainedComponent",
        )
        public class __implementation_package_EffectImpl_Metadata
    """.trimIndent()

    @Test
    fun `compilation of valid effect with metadata option should generate only metadata class`() =
        with(compile(interfaceSource, implementationSource)) {
            assertCompiled()
            assertGeneratedFileCount(1)
            assertGeneratedFile(
                name = "com/uandcode/effects/compiler/common/generated/__implementation_package_EffectImpl_Metadata.kt",
                expectedContent = expectedOutputMetadata,
            )
        }

}

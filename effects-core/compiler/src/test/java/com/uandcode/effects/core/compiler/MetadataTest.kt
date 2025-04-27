package com.uandcode.effects.core.compiler

import com.uandcode.effects.core.compiler.base.AbstractCoreKspTest
import com.uandcode.effects.core.testing.ksp.InputFile
import org.intellij.lang.annotations.Language
import org.junit.Test

class MetadataTest : AbstractCoreKspTest() {

    override val options: Map<String, String> = mutableMapOf(
        "effects.processor.metadata" to "generate"
    )

    private val interfaceSource = InputFile(
        name = "interface_package/Effect.kt",
        content = """
            package interface_package
            import com.uandcode.effects.core.annotations.EffectClass
            interface Effect
        """.trimIndent()
    )

    private val implementationSource = InputFile(
        name = "implementation_package/EffectImpl.kt",
        content = """
            package implementation_package
    
            import com.uandcode.effects.core.annotations.EffectClass
            import interface_package.Effect
    
            @EffectClass
            class EffectImpl : Effect
        """.trimIndent()
    )

    @Language("kotlin")
    private val expectedOutputMetadata = """
        package com.uandcode.effects.compiler.common.generated

        import com.uandcode.effects.core.annotations.EffectMetadata

        @EffectMetadata(
            interfaceClassNames = ["interface_package.Effect"],
            implementationClassName = "implementation_package.EffectImpl",
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

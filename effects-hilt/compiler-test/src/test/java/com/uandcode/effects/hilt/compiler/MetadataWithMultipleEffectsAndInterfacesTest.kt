package com.uandcode.effects.hilt.compiler

import com.uandcode.effects.hilt.compiler.base.AbstractHiltKspTest
import com.uandcode.effects.core.testing.ksp.InputFile
import org.intellij.lang.annotations.Language
import org.junit.Test

class MetadataWithMultipleEffectsAndInterfacesTest : AbstractHiltKspTest() {

    override val options: Map<String, String> = mutableMapOf(
        "effects.processor.metadata" to "generate"
    )

    private val interface1Source = InputFile(
        name = "interface1_package/Effect1.kt",
        content = """
            package interface1_package
            import com.uandcode.effects.hilt.annotations.HiltEffect
            interface Effect1
        """.trimIndent()
    )

    private val interface2Source = InputFile(
        name = "interface2_package/Effect2.kt",
        content = """
            package interface2_package
            import com.uandcode.effects.hilt.annotations.HiltEffect
            interface Effect2
        """.trimIndent()
    )

    private val interface3Source = InputFile(
        name = "interface3_package/Effect3.kt",
        content = """
            package interface3_package
            import com.uandcode.effects.hilt.annotations.HiltEffect
            interface Effect3
        """.trimIndent()
    )

    private val leftImplementationSource = InputFile(
        name = "implementation_package/left/LeftEffectImpl.kt",
        content = """
            package implementation_package.left
    
            import com.uandcode.effects.hilt.annotations.HiltEffect
            import interface1_package.Effect1
            import interface2_package.Effect2
    
            @HiltEffect
            class LeftEffectImpl : Effect1, Effect2
        """.trimIndent()
    )

    private val rightImplementationSource = InputFile(
        name = "implementation_package/right/RightEffectImpl.kt",
        content = """
            package implementation_package.right
    
            import com.uandcode.effects.hilt.annotations.HiltEffect
            import interface2_package.Effect2
            import interface3_package.Effect3
    
            @HiltEffect
            class RightEffectImpl : Effect2, Effect3
        """.trimIndent()
    )

    @Language("kotlin")
    private val expectedLeftOutputMetadata = """
        package com.uandcode.effects.compiler.common.generated

        import com.uandcode.effects.hilt.annotations.HiltEffectMetadata

        @HiltEffectMetadata(
            interfaceClassNames = ["interface1_package.Effect1", "interface2_package.Effect2"],
            implementationClassName = "implementation_package.left.LeftEffectImpl",
            hiltComponentClassName = "dagger.hilt.android.components.ActivityRetainedComponent",
        )
        public class __implementation_package_left_LeftEffectImpl_Metadata
    """.trimIndent()

    @Language("kotlin")
    private val expectedRightOutputMetadata = """
        package com.uandcode.effects.compiler.common.generated

        import com.uandcode.effects.hilt.annotations.HiltEffectMetadata

        @HiltEffectMetadata(
            interfaceClassNames = ["interface2_package.Effect2", "interface3_package.Effect3"],
            implementationClassName = "implementation_package.right.RightEffectImpl",
            hiltComponentClassName = "dagger.hilt.android.components.ActivityRetainedComponent",
        )
        public class __implementation_package_right_RightEffectImpl_Metadata
    """.trimIndent()

    @Test
    fun `compilation of valid effect with metadata option should generate only metadata class`() =
        with(compile(interface1Source, interface2Source, interface3Source, leftImplementationSource, rightImplementationSource)) {
            assertCompiled()
            assertGeneratedFileCount(2)
            assertGeneratedFile(
                name = "com/uandcode/effects/compiler/common/generated/__implementation_package_left_LeftEffectImpl_Metadata.kt",
                expectedContent = expectedLeftOutputMetadata
            )
            assertGeneratedFile(
                name = "com/uandcode/effects/compiler/common/generated/__implementation_package_right_RightEffectImpl_Metadata.kt",
                expectedContent = expectedRightOutputMetadata
            )
        }

}

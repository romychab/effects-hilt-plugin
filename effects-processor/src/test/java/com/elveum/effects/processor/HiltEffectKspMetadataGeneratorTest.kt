package com.elveum.effects.processor

import com.elveum.effects.processor.base.AbstractKspTest
import com.elveum.effects.processor.base.runGenerationTest
import org.junit.Test

class HiltEffectKspMetadataGeneratorTest : AbstractKspTest("custom-effect/generation/metadata") {

    @Test
    fun generator_withDefaultArgs_generatesMetadata() = runGenerationTest {
        compileResourceSubPackage("default_args")

        assertGeneratedFile("__test_TestClass_Metadata.kt", "Output.kt")
    }

    @Test
    fun generator_withCustomArgs_generatesMetadata() = runGenerationTest {
        compileResourceSubPackage("non_default_args")

        assertGeneratedFile("__test_TestClass_Metadata.kt", "Output.kt")
    }

    @Test
    fun generator_withMultipleEffectImplementations_generatesMetadata() = runGenerationTest {
        compileResourceSubPackage("multiple_implementations")

        assertGeneratedFile("__test_TestClass1_Metadata.kt", "Output1.kt")
        assertGeneratedFile("__test_TestClass2_Metadata.kt", "Output2.kt")
    }


}
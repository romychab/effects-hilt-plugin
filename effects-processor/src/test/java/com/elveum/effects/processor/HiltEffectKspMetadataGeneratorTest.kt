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

    @Test
    fun generator_withNonDeclaredCustomCleanUpMethod_fails() = runGenerationTest {
        compileResourceSubPackage("non_declared_custom_cleanup")

        assertCompilationFails("Input.kt:15: Clean-up function 'nonDefaultCleanUp' is specified in TestClass, but not declared in the target interface: TestInterface")
    }

    @Test
    fun generator_withDifferentCleanUpMethods_fails() = runGenerationTest {
        compileResourceSubPackage("multiple_implementations_with_diff_cleanup")

        assertCompilationFails("Input.kt:8: Interface 'TestInterface' has implementations with different 'cleanUpMethodName' annotation args: 1) TestClass1: cleanUpMethodName=clear1; 2) TestClass2: cleanUpMethodName=clear2.")
    }

    @Test
    fun generator_withDifferentComponents_fails() = runGenerationTest {
        compileResourceSubPackage("multiple_implementations_with_diff_components")

        assertCompilationFails("Input.kt:8: Interface 'TestInterface' has implementations installed in different components: 1) TestClass1 is installed in ActivityComponent; 2) TestClass2 is installed in SingletonComponent.")
    }

}
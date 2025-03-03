package com.elveum.effects.processor

import com.elveum.effects.processor.base.AbstractKspTest
import com.elveum.effects.processor.base.runGenerationTest
import org.junit.Test

class HiltEffectKspHiltModuleGeneratorTest : AbstractKspTest("custom-effect/generation/modules") {

    @Test
    fun test_withDefaultInstallInParameter() = runGenerationTest {
        compileResourceSubPackage(packageName = "default_hilt_component")

        assertGeneratedFile("TestInterfaceEffectModule.kt", "OutputInterfaceModule.kt")
        assertGeneratedFile("TestClassImplModule.kt", "OutputImplModule.kt")
    }

    @Test
    fun test_withActivityRetainedComponentInstallInParameter() = runGenerationTest {
        compileResourceSubPackage(packageName = "activity_retained_component")

        assertGeneratedFile("TestInterfaceEffectModule.kt", "OutputInterfaceModule.kt")
        assertGeneratedFile("TestClassImplModule.kt", "OutputImplModule.kt")
    }

    @Test
    fun test_withSingletonComponentInstallInParameter() = runGenerationTest {
        compileResourceSubPackage(packageName = "singleton_component")

        assertGeneratedFile("TestInterfaceEffectModule.kt", "OutputInterfaceModule.kt")
        assertGeneratedFile("TestClassImplModule.kt", "OutputImplModule.kt")
    }

    @Test
    fun test_withActivityComponentInstallInParameter() = runGenerationTest {
        compileResourceSubPackage(packageName = "activity_component")

        assertGeneratedFile("TestInterfaceEffectModule.kt", "OutputInterfaceModule.kt")
        assertGeneratedFile("TestClassImplModule.kt", "OutputImplModule.kt")
    }

    @Test
    fun test_withFragmentComponentInstallInParameter() = runGenerationTest {
        compileResourceSubPackage(packageName = "fragment_component")

        assertGeneratedFile("TestInterfaceEffectModule.kt", "OutputInterfaceModule.kt")
        assertGeneratedFile("TestClassImplModule.kt", "OutputImplModule.kt")
    }

    @Test
    fun test_withInvalidComponentInInstallInParameter_fails() = runGenerationTest {
        compileResourceSubPackage(packageName = "invalid_component")

        assertCompilationFails("Input.kt:9: Invalid installIn parameter value in @HiltEffect(installIn = ...) annotation. Valid values are: @SingletonComponent, @ActivityRetainedComponent, @ActivityComponent and @FragmentComponent.")
    }

    @Test
    fun test_withInconsistentComponents_fails() = runGenerationTest {
        compileResourceSubPackage(packageName = "inconsistent_components")

        assertCompilationFails("Input.kt:6: Interface 'TestInterface' has implementations installed in different components: 1) TestClass1 is installed in SingletonComponent; 2) TestClass2 is installed in ActivityRetainedComponent.")
    }

    @Test
    fun test_withoutApp_shouldNotGenerateInterfaceMediator() = runGenerationTest {
        compileResourceSubPackage(packageName = "test_without_app")

        assertGeneratedFile("TestClassImplModule.kt", "OutputImplModule.kt")
        assertFileIsNotGenerated("TestInterfaceEffectModule.kt")
    }

    @Test
    fun test_withMultipleEffectImplementations() = runGenerationTest {
        compileResourceSubPackage(packageName = "multiple_implementations")

        assertGeneratedFile("TestInterfaceEffectModule.kt", "OutputInterfaceModule.kt")
        assertGeneratedFile("TestClass1ImplModule.kt", "OutputImpl1Module.kt")
        assertGeneratedFile("TestClass2ImplModule.kt", "OutputImpl2Module.kt")
    }

}
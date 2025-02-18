package com.elveum.effects.processor

import com.elveum.effects.processor.base.AbstractKspTest
import com.elveum.effects.processor.base.runGenerationTest
import org.junit.Test

class CustomEffectKspHiltModuleGeneratorTest : AbstractKspTest("custom-effect/generation/module") {

    @Test
    fun test_withDefaultInstallInParameter() = runGenerationTest {
        compileResourceSubPackage(packageName = "default_hilt_component")

        assertGeneratedFile("TestInterfaceModule.kt")
    }

    @Test
    fun test_withActivityRetainedComponentInstallInParameter() = runGenerationTest {
        compileResourceSubPackage(packageName = "activity_retained_component")

        assertGeneratedFile("TestInterfaceModule.kt")
    }

    @Test
    fun test_withSingletonComponentInstallInParameter() = runGenerationTest {
        compileResourceSubPackage(packageName = "singleton_component")

        assertGeneratedFile("TestInterfaceModule.kt")
    }

    @Test
    fun test_withActivityComponentInstallInParameter() = runGenerationTest {
        compileResourceSubPackage(packageName = "activity_component")

        assertGeneratedFile("TestInterfaceModule.kt")
    }

    @Test
    fun test_withFragmentComponentInstallInParameter() = runGenerationTest {
        compileResourceSubPackage(packageName = "fragment_component")

        assertGeneratedFile("TestInterfaceModule.kt")
    }

    @Test
    fun test_withInvalidComponentInInstallInParameter_fails() = runGenerationTest {
        compileResourceSubPackage(packageName = "invalid_component")

        assertCompilationFails("Input.kt:8: Invalid installIn parameter value in @CustomEffect(installIn = ...) annotation. Valid values are: @SingletonComponent, @ActivityRetainedComponent, @ActivityComponent and @FragmentComponent.")
    }

}
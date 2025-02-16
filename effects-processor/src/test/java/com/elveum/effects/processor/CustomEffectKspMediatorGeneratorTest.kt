@file:OptIn(ExperimentalCompilerApi::class)

package com.elveum.effects.processor

import com.elveum.effects.processor.base.AbstractKspTest
import com.elveum.effects.processor.base.runGenerationTest
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

class CustomEffectKspMediatorGeneratorTest : AbstractKspTest("custom-effect/generation/mediator") {

    @Test
    fun testUnitCommandImplementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "unit_command")

        assertGeneratedFile("TestInterfaceMediator.kt")
    }

    @Test
    fun testCoroutineCommandImplementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "coroutine_command")

        assertGeneratedFile("TestInterfaceMediator.kt")
    }

    @Test
    fun testFlowCommandImplementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "flow_command")

        assertGeneratedFile("TestInterfaceMediator.kt")
    }

    @Test
    fun testUnitCommand_withTypeParams_implementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "unit_command_with_type_params")

        assertGeneratedFile("TestInterfaceMediator.kt")
    }

    @Test
    fun testCoroutineCommand_withTypeParams_implementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "coroutine_command_with_type_params")

        assertGeneratedFile("TestInterfaceMediator.kt")
    }

    @Test
    fun testFlowCommand_withTypeParams_implementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "flow_command_with_type_params")

        assertGeneratedFile("TestInterfaceMediator.kt")
    }

    @Test
    fun testUnitCommand_withVarargParam_implementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "vararg_params")

        assertGeneratedFile("TestInterfaceMediator.kt")
    }

    @Test
    fun defaultMethods_areIgnored() = runGenerationTest {
        compileResourceSubPackage(packageName = "default_methods_are_ignored")

        assertGeneratedFile("TestInterfaceMediator.kt")
    }

    @Test
    fun unitCommand_withReturnType_fails() = runGenerationTest {
        compileResourceSubPackage(packageName = "unit_command_with_return_type_fails")

        assertCompilationFails("Input.kt:4: Non-suspend methods can't have a return type")
    }

    @Test
    fun testCommands_fromSuperInterface_implementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "commands_from_superinterface_impl")

        assertGeneratedFile("TestInterfaceMediator.kt")
    }

}
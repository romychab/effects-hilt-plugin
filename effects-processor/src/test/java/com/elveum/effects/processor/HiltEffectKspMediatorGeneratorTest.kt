package com.elveum.effects.processor

import com.elveum.effects.processor.base.AbstractKspTest
import com.elveum.effects.processor.base.runGenerationTest
import org.junit.Test

class HiltEffectKspMediatorGeneratorTest : AbstractKspTest("custom-effect/generation/mediator") {

    @Test
    fun testUnitCommandImplementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "unit_command")

        assertGeneratedFile("__TestInterfaceMediator.kt")
    }

    @Test
    fun testCoroutineCommandImplementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "coroutine_command")

        assertGeneratedFile("__TestInterfaceMediator.kt")
    }

    @Test
    fun testFlowCommandImplementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "flow_command")

        assertGeneratedFile("__TestInterfaceMediator.kt")
    }

    @Test
    fun testUnitCommand_withTypeParams_implementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "unit_command_with_type_params")

        assertGeneratedFile("__TestInterfaceMediator.kt")
    }

    @Test
    fun testCoroutineCommand_withTypeParams_implementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "coroutine_command_with_type_params")

        assertGeneratedFile("__TestInterfaceMediator.kt")
    }

    @Test
    fun testFlowCommand_withTypeParams_implementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "flow_command_with_type_params")

        assertGeneratedFile("__TestInterfaceMediator.kt")
    }

    @Test
    fun testUnitCommand_withVarargParam_implementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "vararg_params")

        assertGeneratedFile("__TestInterfaceMediator.kt")
    }

    @Test
    fun defaultMethods_areIgnored() = runGenerationTest {
        compileResourceSubPackage(packageName = "default_methods_are_ignored")

        assertGeneratedFile("__TestInterfaceMediator.kt")
    }

    @Test
    fun testCommands_fromSuperInterface_implementation() = runGenerationTest {
        compileResourceSubPackage(packageName = "commands_from_superinterface_impl")

        assertGeneratedFile("__TestInterfaceMediator.kt")
    }

    @Test
    fun unitCommand_withReturnType_fails() = runGenerationTest {
        compileResourceSubPackage(packageName = "unit_command_with_return_type_fails")

        assertCompilationFails("Input.kt:5: Non-suspend methods can't have a return type")
    }

    @Test
    fun test_withoutApp_doesNotGenerateMediator() = runGenerationTest {
        compileResourceSubPackage(packageName = "test_without_app")

        assertCompilationCompletes()
        assertFileIsNotGenerated("__TestInterfaceMediator.kt")
    }

}
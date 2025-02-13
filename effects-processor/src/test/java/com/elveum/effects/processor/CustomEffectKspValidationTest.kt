@file:OptIn(ExperimentalCompilerApi::class)

package com.elveum.effects.processor

import com.elveum.effects.processor.base.AbstractKspTest
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomEffectKspValidationTest : AbstractKspTest("custom-effect/validation") {

    @Test
    fun abstractClass_fails() {
        val result = compileSourceFile("TestEmptyAbstractClass.kt")

        result.assertErrorLogged("TestEmptyAbstractClass.kt:4: Class annotated with @CustomEffect should not be abstract")
        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
    }

    @Test
    fun class_fails() {
        val result = compileSourceFile("TestEmptyClass.kt")

        result.assertErrorLogged("TestEmptyClass.kt:4: Class annotated with @CustomEffect should implement at least one interface")
        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
    }

    @Test
    fun interface_fails() {
        val result = compileSourceFile("TestInterface.kt")

        result.assertErrorLogged("TestInterface.kt:6: Symbol annotated with @CustomEffect should be a Class or Object")
        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
    }

    @Test
    fun enumClass_fails() {
        val result = compileSourceFile("TestEnumClass.kt")

        result.assertErrorLogged("TestEnumClass.kt:6: Symbol annotated with @CustomEffect should be a Class or Object")
        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
    }

    @Test
    fun sealedClass_fails() {
        val result = compileSourceFile("TestSealedClass.kt")

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
        result.assertErrorLogged("TestSealedClass.kt:6: Symbol annotated with @CustomEffect should be a Class or Object")
    }

    @Test
    fun classWithTypeParameters_fails() {
        val result = compileSourceFile("TestClassWithTypeParameters.kt")

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
        result.assertErrorLogged("TestClassWithTypeParameters.kt:6: Class annotated with @CustomEffect should not have type parameters")
    }

    @Test
    fun interfaceWithTypeParameters_fails() {
        val result = compileSourceFile("TestInterfaceWithTypeParameters.kt")

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
        result.assertErrorLogged("TestInterfaceWithTypeParameters.kt:6: Class annotated with @CustomEffect should not have a target interface 'BaseInterface' with type parameters")
    }

    @Test
    fun classWithInvalidTargetInterface_fails() {
        val result = compileSourceFile("TestClassWithInvalidTargetInterface.kt")

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
        result.assertErrorLogged("TestClassWithInvalidTargetInterface.kt:12: @CustomEffect(target = ...) parameter can be set only to these values: ValidInterface1::class, ValidInterface2::class")
    }

    @Test
    fun classWithMoreThanOneInterface_withoutTargetParameter_fails() {
        val result = compileSourceFile("TestClassWithMoreThanOneInterface.kt")

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
        result.assertErrorLogged("TestClassWithMoreThanOneInterface.kt:6: @CustomEffect(target = ...) parameter should be specified if your class implements more than 1 interface")
    }

    @Test
    fun effect_withMultipleImplementations_fails() {
        val result = compileSourceFile("TestMultipleEffectImplementations.kt")

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
        result.assertErrorLogged("TestMultipleEffectImplementations.kt:3: Target interface 'EffectInterface' should have only one implementation. Current implementations: EffectImplementation1, EffectImplementation2")
    }

    @Test
    fun nonTopLevelClass_fails() {
        val result = compileSourceFile("TestNonTopLevelClass.kt")

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
        result.assertErrorLogged("TestNonTopLevelClass.kt:8: Class annotated with @CustomEffect should be a top-level class")
    }

    @Test
    fun nonTopLevelInterface_fails() {
        val result = compileSourceFile("TestNonTopLevelInterface.kt")

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode)
        result.assertErrorLogged("TestNonTopLevelInterface.kt:11: Class annotated with @CustomEffect should not implement nested interface")
    }

}

package com.elveum.effects.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

/**
 * Types for usage upon generating Kotlin code (Kotlin Poet).
 */
object KNames {
    private const val coroutinesXPackage = Names.coroutinesXPackage
    private const val flowPackage = Names.flowPackage
    private const val corePackage = Names.corePackage
    private const val actorsPackage = Names.actorsPackage

    val coroutineScope = ClassName(coroutinesXPackage, "CoroutineScope")
    val qualifier = ClassName("javax.inject", "Qualifier")
    val rawFlow = ClassName(flowPackage, "Flow")
    val any = ClassName("kotlin", "Any")
    fun sideEffectMediator(parametrizedType: TypeName): ParameterizedTypeName {
        val rawType = ClassName(
            actorsPackage,
            "SideEffectMediator"
        )
        return rawType.parameterizedBy(parametrizedType)
    }

    fun commandProcessor(parametrizedType: TypeName): ParameterizedTypeName {
        val rawType = ClassName(
            corePackage,
            "CommandProcessor"
        )
        return rawType.parameterizedBy(parametrizedType)
    }
    fun unitCommand(type: TypeName): ParameterizedTypeName {
        return ClassName(corePackage, "UnitCommand")
            .parameterizedBy(type)
    }
    fun coroutineCommand(inputType: TypeName, returnType: TypeName): ParameterizedTypeName {
        return ClassName(corePackage, "CoroutineCommand")
            .parameterizedBy(inputType, returnType)
    }
    fun flowCommand(inputType: TypeName, returnType: TypeName): ParameterizedTypeName {
        return ClassName(corePackage, "FlowCommand")
            .parameterizedBy(inputType, returnType)
    }
    fun flow(type: TypeName): ParameterizedTypeName {
        return ClassName(flowPackage, "Flow")
            .parameterizedBy(type)
    }
}
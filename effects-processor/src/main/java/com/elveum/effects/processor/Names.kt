package com.elveum.effects.processor

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName

/**
 * Types for usage upon generating Java code (Java Poet).
 */
object Names {
    const val corePackage = "com.elveum.effects.core"
    const val actorsPackage = "$corePackage.actors"
    const val coroutinesXPackage = "kotlinx.coroutines"
    const val flowPackage = "$coroutinesXPackage.flow"

    private const val daggerPackage = "dagger"
    private const val daggerMultiBindingPackage = "$daggerPackage.multibindings"
    private const val hiltPackage = "dagger.hilt"
    private const val hiltScopePackage = "$hiltPackage.android.scopes"
    private const val hiltAndroidComponentsPackage = "$hiltPackage.android.components"
    private const val coroutinesPackage = "kotlin.coroutines"
    private const val coreDiPackage = "$corePackage.di"

    val providesAnnotation: ClassName = ClassName.get(daggerPackage, "Provides")
    val moduleAnnotation: ClassName = ClassName.get(daggerPackage, "Module")
    val installInAnnotation: ClassName = ClassName.get(hiltPackage, "InstallIn")
    val activityRetainedComponentName: ClassName = ClassName.get(hiltAndroidComponentsPackage, "ActivityRetainedComponent")
    val activityComponentName: ClassName = ClassName.get(hiltAndroidComponentsPackage, "ActivityComponent")
    val activityRetainedScope: ClassName = ClassName.get(hiltScopePackage, "ActivityRetainedScoped")
    val activityScope: ClassName = ClassName.get(hiltScopePackage, "ActivityScoped")
    val intoSet: ClassName = ClassName.get(daggerMultiBindingPackage, "IntoSet")
    val sideEffectsScope: ClassName = ClassName.get(coreDiPackage, "SideEffectsMediatorScope")
    val coroutineScope: ClassName = ClassName.get(coroutinesXPackage, "CoroutineScope")
    val sidePair: ClassName = ClassName.get(actorsPackage, "SidePair")
    val sideImplementation: ClassName = ClassName.get(actorsPackage, "SideEffectImplementation")
    val flow: ClassName = ClassName.get(flowPackage, "Flow")
    val retainedData = ClassName.get("${corePackage}.retain", "RetainedData")
    val wrappedRetainedData = ClassName.get("${corePackage}.retain", "WrappedRetainedData")
    fun continuation(parametrizedType: TypeName): ParameterizedTypeName {
        val rawType = ClassName.get(
            coroutinesPackage,
            "Continuation"
        )
        return ParameterizedTypeName.get(rawType, parametrizedType)
    }
    fun sideEffectMediator(parametrizedType: TypeName): ParameterizedTypeName {
        val rawType = ClassName.get(
            actorsPackage,
            "SideEffectMediator"
        )
        return ParameterizedTypeName.get(rawType, parametrizedType)
    }
}
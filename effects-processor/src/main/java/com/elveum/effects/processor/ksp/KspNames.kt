package com.elveum.effects.processor.ksp

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

object KspNames {
    const val sideEffectShortAnnotationName = "SideEffect"
    const val sideEffectAnnotationName = "com.elveum.effects.annotations.$sideEffectShortAnnotationName"
    const val flow = "kotlinx.coroutines.flow.Flow"

    val moduleAnnotation: ClassName = ClassName("dagger", "Module")
    val installInAnnotation: ClassName = ClassName("dagger.hilt", "InstallIn")
    val activityRetainedComponentName = ClassName("dagger.hilt.android.components", "ActivityRetainedComponent")
    val coroutineScope: ClassName = ClassName("kotlinx.coroutines", "CoroutineScope")
    val sideEffectsScope: ClassName = ClassName("com.elveum.effects.core.di", "SideEffectsMediatorScope")
    val providesAnnotation: ClassName = ClassName("dagger", "Provides")
    val activityRetainedScope: ClassName = ClassName("dagger.hilt.android.scopes", "ActivityRetainedScoped")
    val qualifier = ClassName("javax.inject", "Qualifier")
    val intoSet: ClassName = ClassName("dagger.multibindings", "IntoSet")
    val sidePair: ClassName = ClassName("com.elveum.effects.core.actors", "SidePair")
    val activityComponentName: ClassName = ClassName("dagger.hilt.android.components.", "ActivityComponent")
    val activityScope: ClassName = ClassName("dagger.hilt.android.scopes", "ActivityScoped")
    val retainedData = ClassName("com.elveum.effects.core.retain", "RetainedData")
    val wrappedRetainedData = ClassName("com.elveum.effects.core.retain", "WrappedRetainedData")
    val sideImplementation: ClassName = ClassName("com.elveum.effects.core.actors", "SideEffectImplementation")

    fun sideEffectMediator(parametrizedType: TypeName): ParameterizedTypeName {
        val rawType = ClassName(
            "com.elveum.effects.core.actors",
            "SideEffectMediator"
        )
        return rawType.parameterizedBy(parametrizedType)
    }
}

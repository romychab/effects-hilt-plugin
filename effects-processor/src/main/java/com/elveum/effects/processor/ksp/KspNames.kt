package com.elveum.effects.processor.ksp

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

object KspNames {

    @Deprecated("Use mviEffectShortAnnotationName")
    const val deprecatedShortAnnotationName = "SideEffect"
    const val mviEffectShortAnnotationName = "MviEffect"

    @Deprecated("Use mviEffectAnnotationName")
    const val deprecatedAnnotationName = "com.elveum.effects.annotations.$deprecatedShortAnnotationName"
    const val mviEffectAnnotationName = "com.elveum.effects.annotations.$mviEffectShortAnnotationName"

    const val flow = "kotlinx.coroutines.flow.Flow"

    val moduleAnnotation: ClassName = ClassName("dagger", "Module")
    val installInAnnotation: ClassName = ClassName("dagger.hilt", "InstallIn")
    val activityRetainedComponentName = ClassName("dagger.hilt.android.components", "ActivityRetainedComponent")
    val coroutineScope: ClassName = ClassName("kotlinx.coroutines", "CoroutineScope")
    val mviEffectsScope: ClassName = ClassName("com.elveum.effects.core.di", "MviEffectsMediatorScope")
    val providesAnnotation: ClassName = ClassName("dagger", "Provides")
    val activityRetainedScope: ClassName = ClassName("dagger.hilt.android.scopes", "ActivityRetainedScoped")
    val qualifier = ClassName("javax.inject", "Qualifier")
    val intoSet: ClassName = ClassName("dagger.multibindings", "IntoSet")
    val mviPair: ClassName = ClassName("com.elveum.effects.core.actors", "MviPair")
    val activityComponentName: ClassName = ClassName("dagger.hilt.android.components.", "ActivityComponent")
    val activityScope: ClassName = ClassName("dagger.hilt.android.scopes", "ActivityScoped")
    val retainedData = ClassName("com.elveum.effects.core.retain", "RetainedData")
    val wrappedRetainedData = ClassName("com.elveum.effects.core.retain", "WrappedRetainedData")
    val mviImplementation: ClassName = ClassName("com.elveum.effects.core.actors", "MviEffectImplementation")

    fun mviEffectMediator(parametrizedType: TypeName): ParameterizedTypeName {
        val rawType = ClassName(
            "com.elveum.effects.core.actors",
            "MviEffectMediator"
        )
        return rawType.parameterizedBy(parametrizedType)
    }
}

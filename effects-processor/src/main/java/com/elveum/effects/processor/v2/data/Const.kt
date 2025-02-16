@file:Suppress("ConstPropertyName")

package com.elveum.effects.processor.v2.data

import com.elveum.effects.annotations.CustomEffect
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName

object Const {
    private const val CorePackage = "com.elveum.effects.core.v2"

    val CustomEffectAnnotationName: ClassName = CustomEffect::class.asClassName()
    val FlowClassName = ClassName("kotlinx.coroutines.flow", "Flow")
    val DefineComponentName = ClassName("dagger.hilt", "DefineComponent")

    val ActivityRetainedComponentName = ClassName("dagger.hilt.android.components", "ActivityRetainedComponent")
    val ActivityRetainedScope = ClassName("dagger.hilt.android.scopes", "ActivityRetainedScoped")
    val JavaxScopeAnnotationName = ClassName("javax.inject", "Scope")
    val JakartaScopeAnnotationName = ClassName("jakarta.inject", "Scope")

    const val TargetArgument: String = "target"
    const val InstallInArgument: String = "installIn"

    fun commandExecutorName(
        className: ClassName,
    ): ParameterizedTypeName {
        val rawType = ClassName(CorePackage, "CommandExecutor")
        return rawType.parameterizedBy(className)
    }
}

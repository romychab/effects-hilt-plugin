@file:Suppress("ConstPropertyName")

package com.elveum.effects.processor.data

import com.elveum.effects.annotations.HiltEffect
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName

object Const {
    private const val CorePackage = "com.elveum.effects.core"

    val HiltEffectAnnotationName: ClassName = HiltEffect::class.asClassName()
    val HiltAppAnnotationName: ClassName = ClassName("dagger.hilt.android", "HiltAndroidApp")
    val FlowClassName = ClassName("kotlinx.coroutines.flow", "Flow")
    val DefineComponentName = ClassName("dagger.hilt", "DefineComponent")

    val ActivityRetainedComponentName = ClassName("dagger.hilt.android.components", "ActivityRetainedComponent")
    val SingletonComponentName = ClassName("dagger.hilt.components", "SingletonComponent")
    val ActivityRetainedScope = ClassName("dagger.hilt.android.scopes", "ActivityRetainedScoped")
    val JavaxScopeAnnotationName = ClassName("javax.inject", "Scope")
    val JakartaScopeAnnotationName = ClassName("jakarta.inject", "Scope")
    val JavaxInjectAnnotationName = ClassName("javax.inject", "Inject")
    val ModuleAnnotationName: ClassName = ClassName("dagger", "Module")
    val InstallInAnnotationName: ClassName = ClassName("dagger.hilt", "InstallIn")
    val ProvidesAnnotationName: ClassName = ClassName("dagger", "Provides")
    val IntoSetAnnotationName: ClassName = ClassName("dagger.multibindings", "IntoSet")
    val EffectControllerImplName: ClassName = ClassName("$CorePackage.impl.", "EffectControllerImpl")
    val EffectRecordName: ClassName = ClassName(CorePackage, "EffectRecord")

    const val TargetArgument: String = "target"
    const val TargetArrayArgument: String = "targets"

    const val InstallInArgument: String = "installIn"
    const val CleanUpMethodNameArgument: String = "cleanUpMethodName"
    const val CleanUpMethodNameArgumentDefaultValue: String = "cleanUp"

    const val MetadataPackage: String = "hilt_effects_plugin"
    const val TargetInterfaceMetadataAnnotation: String = "TargetInterfaceMetadata"
    const val MetadataInterfaceClassnames: String = "interfaceClassNames"
    const val MetadataImplClassname: String = "implementationClassName"
    const val MetadataHiltComponent: String = "hiltComponentClassName"
    const val MetadataHiltScope: String = "hiltScopeClassName"
    const val MetadataCleanUpMethodName: String = "cleanUpMethodName"

    const val SingletonPriorityConstName = "SINGLETON_PRIORITY"
    const val ActivityRetainedPriorityConstName = "ACTIVITY_RETAINED_PRIORITY"
    const val ViewModelPriorityConstName = "VIEW_MODEL_PRIORITY"
    const val OtherPriorityConstName = "OTHER_PRIORITY"

    fun commandExecutorName(
        className: ClassName,
    ): ParameterizedTypeName {
        val rawType = ClassName(CorePackage, "CommandExecutor")
        return rawType.parameterizedBy(className)
    }

    fun effectControllerName(
        className: ClassName,
    ): ParameterizedTypeName {
        val rawType = ClassName(CorePackage, "EffectController")
        return rawType.parameterizedBy(className)
    }

    fun observableResourceStoreName(
        className: ClassName,
    ): ParameterizedTypeName {
        val rawType = ClassName(CorePackage, "ObservableResourceStore")
        return rawType.parameterizedBy(className)
    }

    fun providerName(
        className: TypeName,
    ): ParameterizedTypeName {
        val rawType = ClassName("javax.inject", "Provider")
        return rawType.parameterizedBy(className)
    }

}

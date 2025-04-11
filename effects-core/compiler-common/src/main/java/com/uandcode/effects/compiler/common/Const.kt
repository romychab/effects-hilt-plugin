package com.uandcode.effects.compiler.common

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName

internal object Const {

    val TargetArrayArgument = "targets"
    val AutoCloseableClassName = AutoCloseable::class.asClassName()
    val EffectProxyMarker = ClassName("com.uandcode.effects.core", "EffectProxyMarker")

    val MetadataInterfaceClassNames: String = "interfaceClassNames"
    val MetadataImplementationClassName: String = "implementationClassName"

    val MetadataPackage: String = "com.uandcode.effects.compiler.common.generated"

    val FlowClassName = ClassName("kotlinx.coroutines.flow", "Flow")

    val ProxyEffectStoreInterfaceClassName = ClassName("com.uandcode.effects.stub.api", "ProxyEffectStore")
    val InternalProxyEffectStoreImplementationClassName = ClassName("com.uandcode.effects.core.internal", "InternalProxyEffectStoreImpl")
    val GeneratedProxyEffectStoreClassName = ClassName("com.uandcode.effects.core.kspcontract", "AnnotationBasedProxyEffectStore")
    val ProxyConfigurationClassName = ClassName("com.uandcode.effects.stub.api", "ProxyConfiguration")

    fun commandExecutorName(
        className: TypeName,
    ): ParameterizedTypeName {
        val rawType = ClassName("com.uandcode.effects.core", "CommandExecutor")
        return rawType.parameterizedBy(className)
    }

}

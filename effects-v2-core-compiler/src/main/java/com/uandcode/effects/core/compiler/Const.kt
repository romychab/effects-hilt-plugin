package com.uandcode.effects.core.compiler

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.internal.InternalProxyEffectStoreImpl
import com.uandcode.effects.stub.api.ProxyConfiguration
import com.uandcode.effects.stub.api.ProxyEffectStore

internal object Const {

    val TargetArgument = "target"
    val AutoCloseableClassName = AutoCloseable::class.asClassName()

    val MetadataInterfaceClassName: String = "interfaceClassName"
    val MetadataImplementationClassName: String = "implementationClassName"

    val MetadataPackage: String = "com.uandcode.effects.core.compiler.generated"

    val FlowClassName = ClassName("kotlinx.coroutines.flow", "Flow")

    val ProxyEffectStoreInterfaceClassName = ProxyEffectStore::class.asClassName()
    val InternalProxyEffectStoreImplementationClassName = InternalProxyEffectStoreImpl::class.asClassName()
    val GeneratedProxyEffectStoreClassName = ClassName("com.uandcode.effects.stub", "AnnotationBasedProxyEffectStore")
    val ProxyConfigurationClassName = ProxyConfiguration::class.asClassName()

    fun commandExecutorName(
        className: TypeName,
    ): ParameterizedTypeName {
        val rawType = CommandExecutor::class.asClassName()
        return rawType.parameterizedBy(className)
    }

}

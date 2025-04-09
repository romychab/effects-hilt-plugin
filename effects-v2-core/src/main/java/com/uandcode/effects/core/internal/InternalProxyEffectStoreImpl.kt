package com.uandcode.effects.core.internal

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.stub.api.ProxyConfiguration
import com.uandcode.effects.stub.api.ProxyDependency
import com.uandcode.effects.stub.api.ProxyEffectStore
import kotlin.reflect.KClass

/**
 * For internal usage!
 *
 * Public modifier is used here because this class is accessed from
 * auto-generated code.
 */
@Suppress("UNCHECKED_CAST")
public class InternalProxyEffectStoreImpl(
    override val proxyConfiguration: ProxyConfiguration = ProxyConfiguration.Default(),
) : ProxyEffectStore {

    private val providerMap = mutableMapOf<KClass<*>, (CommandExecutor<*>) -> Any>()
    private val targetInterfaceClassesMap = mutableMapOf<KClass<*>, MutableSet<KClass<*>>>()

    override val allTargetInterfaces: Set<KClass<*>> by lazy {
        targetInterfaceClassesMap.values.flatten().toSet()
    }

    override fun findTargetInterfaces(clazz: KClass<*>): Set<KClass<*>> {
        val interfaces = targetInterfaceClassesMap[clazz]
        if (interfaces.isNullOrEmpty()) {
            throw EffectNotFoundException(clazz, proxyConfiguration)
        }
        return interfaces
    }

    override fun <T : Any> createProxy(
        clazz: KClass<T>,
        proxyDependency: ProxyDependency,
    ): T {
        val commandExecutor = proxyDependency as CommandExecutor<T>
        val creatorFunction = providerMap[clazz] ?: throw EffectNotFoundException(clazz, proxyConfiguration)
        return creatorFunction.invoke(commandExecutor as CommandExecutor<*>) as T
    }

    public fun <T : Any> registerProxyProvider(
        clazz: KClass<T>,
        provider: (CommandExecutor<T>) -> T,
    ) {
        providerMap[clazz] = provider as (CommandExecutor<*>) -> Any
    }

    public fun registerTarget(
        implClass: KClass<*>,
        targetClass: KClass<*>,
    ) {
        getOrCreateClassSet(implClass).add(targetClass)
        getOrCreateClassSet(targetClass).add(targetClass)
    }

    private fun getOrCreateClassSet(key: KClass<*>): MutableSet<KClass<*>> {
        return targetInterfaceClassesMap[key] ?: mutableSetOf<KClass<*>>().also {
            targetInterfaceClassesMap[key] = it
        }
    }
}

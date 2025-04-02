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
    override val proxyConfiguration: ProxyConfiguration = ProxyConfiguration(),
) : ProxyEffectStore {

    private val providerMap = mutableMapOf<KClass<*>, (CommandExecutor<*>) -> Any>()
    private val targetInterfaceClassesMap = mutableMapOf<KClass<*>, KClass<*>>()

    override val allTargetInterfaces: Set<KClass<*>> by lazy {
        targetInterfaceClassesMap.values.toSet()
    }

    override fun findTargetInterface(clazz: KClass<*>): KClass<*> {
        return targetInterfaceClassesMap[clazz] ?: throw EffectNotFoundException(clazz, proxyConfiguration)
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
        targetInterfaceClassesMap[implClass] = targetClass
        targetInterfaceClassesMap[targetClass] = targetClass
    }

}

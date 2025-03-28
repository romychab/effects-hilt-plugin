package com.uandcode.effects.core.internal

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.ProxyEffectStore
import kotlin.reflect.KClass

/**
 * For internal usage!
 *
 * Public modifier is used here because this class is accessed from
 * auto-generated code.
 */
@Suppress("UNCHECKED_CAST")
public class InternalProxyEffectStoreImpl : ProxyEffectStore {

    private val providerMap = mutableMapOf<KClass<*>, (CommandExecutor<*>) -> Any>()
    private val targetInterfaceClassesMap = mutableMapOf<KClass<*>, KClass<*>>()

    override val allTargetInterfaces: Set<KClass<*>> by lazy {
        targetInterfaceClassesMap.values.toSet()
    }

    override fun findTargetInterface(clazz: KClass<*>): KClass<*> {
        return targetInterfaceClassesMap[clazz] ?: throwException(clazz)
    }

    override fun <T : Any> createProxy(
        clazz: KClass<T>,
        commandExecutor: CommandExecutor<T>,
    ): T {
        val creatorFunction = providerMap[clazz] ?: throwException(clazz)
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

    private fun throwException(clazz: KClass<*>): Nothing {
        throw IllegalArgumentException("Can't find an effect for '$clazz'. Make sure you use correct annotations.")
    }
}

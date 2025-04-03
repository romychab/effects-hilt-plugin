package com.uandcode.effects.core.internal

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.ProxyEffectFactory
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.internal.ProxyEffectStoreProvider.getGeneratedProxyEffectStore
import com.uandcode.effects.stub.api.ProxyConfiguration
import kotlin.reflect.KClass

internal object DefaultProxyEffectFactory : ProxyEffectFactory {

    override val proxyConfiguration: ProxyConfiguration
        get() = getGeneratedProxyEffectStore().proxyConfiguration

    override fun findTargetInterface(clazz: KClass<*>): KClass<*> {
        return getGeneratedProxyEffectStore().findTargetInterface(clazz)
            ?: throw EffectNotFoundException(clazz, proxyConfiguration)
    }

    override fun <T : Any> createProxy(clazz: KClass<T>, commandExecutor: CommandExecutor<T>): T {
        return getGeneratedProxyEffectStore().createProxy(clazz, commandExecutor)
    }

}

package com.uandcode.effects.core.factories

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.GeneratedProxyEffectStoreProvider.getGeneratedProxyEffectStore
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.stub.api.ProxyConfiguration
import com.uandcode.effects.stub.api.ProxyEffectStore
import kotlin.reflect.KClass

public class DefaultProxyEffectFactory(
    private val proxyEffectStore: ProxyEffectStore = getGeneratedProxyEffectStore(),
) : ProxyEffectFactory {

    override val proxyConfiguration: ProxyConfiguration
        get() = proxyEffectStore.proxyConfiguration

    override fun findTargetInterface(clazz: KClass<*>): KClass<*> {
        return proxyEffectStore.findTargetInterface(clazz)
            ?: throw EffectNotFoundException(clazz, proxyConfiguration)
    }

    override fun <T : Any> createProxy(clazz: KClass<T>, commandExecutor: CommandExecutor<T>): T {
        return proxyEffectStore.createProxy(clazz, commandExecutor)
    }

    public companion object {
        public val annotationBasedInstance: ProxyEffectFactory =
            DefaultProxyEffectFactory(getGeneratedProxyEffectStore())
    }
}

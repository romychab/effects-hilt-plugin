package com.uandcode.effects.stub

import com.uandcode.effects.stub.api.InvalidEffectSetupException
import com.uandcode.effects.stub.api.ProxyConfiguration
import com.uandcode.effects.stub.api.ProxyDependency
import com.uandcode.effects.stub.api.ProxyEffectStore
import kotlin.reflect.KClass

/**
 * The body of this object is replaced by KSP after processing
 * all effect annotations (e.g. @EffectClass, @HiltEffect and so on).
 *
 * @see ProxyEffectStore
 */
public object GeneratedProxyEffectStore : ProxyEffectStore {

    override val proxyConfiguration: ProxyConfiguration = ProxyConfiguration()

    /**
     * @see ProxyEffectStore.allTargetInterfaces
     */
    override val allTargetInterfaces: Set<KClass<*>> = emptySet()

    /**
     * @see ProxyEffectStore.createProxy
     */
    override fun <T : Any> createProxy(
        clazz: KClass<T>,
        proxyDependency: ProxyDependency,
    ): T {
        throw InvalidEffectSetupException()
    }

    /**
     * @see ProxyEffectStore.findTargetInterface
     */
    override fun findTargetInterface(clazz: KClass<*>): KClass<*>? {
        throw InvalidEffectSetupException()
    }

}

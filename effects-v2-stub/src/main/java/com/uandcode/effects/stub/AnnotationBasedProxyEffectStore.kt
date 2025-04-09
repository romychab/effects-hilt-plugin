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
public object AnnotationBasedProxyEffectStore : ProxyEffectStore {

    override val proxyConfiguration: ProxyConfiguration = ProxyConfiguration.Default()

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
     * @see ProxyEffectStore.findTargetInterfaces
     */
    override fun findTargetInterfaces(clazz: KClass<*>): Set<KClass<*>> {
        throw InvalidEffectSetupException()
    }

}

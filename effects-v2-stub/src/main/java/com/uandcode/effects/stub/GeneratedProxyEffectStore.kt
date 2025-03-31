package com.uandcode.effects.stub

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.ProxyEffectStore
import com.uandcode.effects.core.exceptions.InvalidEffectSetupException
import kotlin.reflect.KClass

/**
 * The body of this object is replaced by KSP after processing
 * all effect annotations (e.g. @EffectClass, @HiltEffect and so on).
 *
 * @see ProxyEffectStore
 */
public object GeneratedProxyEffectStore : ProxyEffectStore {

    /**
     * @see ProxyEffectStore.allTargetInterfaces
     */
    override val allTargetInterfaces: Set<KClass<*>> = emptySet()

    /**
     * @see ProxyEffectStore.createProxy
     */
    override fun <T : Any> createProxy(
        clazz: KClass<T>,
        commandExecutor: CommandExecutor<T>,
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

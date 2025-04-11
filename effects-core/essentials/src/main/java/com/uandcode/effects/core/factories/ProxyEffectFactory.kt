package com.uandcode.effects.core.factories

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.stub.api.ProxyConfiguration
import kotlin.reflect.KClass

public interface ProxyEffectFactory {

    /**
     * Get the current [ProxyConfiguration]. Configuration may be different
     * depending on the extension being used (Hilt extension, Koin, Runtime, etc.)
     */
    public val proxyConfiguration: ProxyConfiguration

    /**
     * Get a target effect interface definition by [clazz]. The [clazz]
     * argument may be the target interface definition itself, or a
     * definition of annotated subclass.
     */
    public fun findTargetInterfaces(clazz: KClass<*>): Set<KClass<*>>

    /**
     * Create a proxy implementation of the target interface specified
     * by a [clazz] argument. The created proxy implementation must delegate
     * all calls to [commandExecutor] methods.
     *
     * @see CommandExecutor
     */
    public fun <T : Any> createProxy(
        clazz: KClass<T>,
        commandExecutor: CommandExecutor<T>,
    ): T

}

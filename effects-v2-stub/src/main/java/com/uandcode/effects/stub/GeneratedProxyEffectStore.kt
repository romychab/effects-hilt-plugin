package com.uandcode.effects.stub

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.ProxyEffectStore
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
        throwStubException()
    }

    /**
     * @see ProxyEffectStore.findTargetInterface
     */
    override fun findTargetInterface(clazz: KClass<*>): KClass<*>? {
        throwStubException()
    }

    private fun throwStubException(): Nothing {
        throw IllegalStateException("@EffectApplication annotation or KSP annotation processor not found.\n" +
                "1. Make sure you've added @EffectApplication annotation to your main application module\n" +
                "2. Review your dependencies in build.gradle file\n" +
                "3. Do not forget to replace RootEffectComponents in unit tests by using setEmpty() and/or setGlobal()  method")
    }

}

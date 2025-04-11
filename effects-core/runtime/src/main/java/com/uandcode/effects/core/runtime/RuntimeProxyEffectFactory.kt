package com.uandcode.effects.core.runtime

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.factories.ProxyEffectFactory
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.runtime.proxy.ProxyMethodInterceptor
import com.uandcode.effects.stub.api.ProxyConfiguration
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

/**
 * A [ProxyEffectFactory] implementation that uses ByteBuddy to generate
 * and load proxy implementations at runtime.
 *
 * Usually you don't need to use this class directly. See [RuntimeEffectScopes] instead.
 */
public class RuntimeProxyEffectFactory : ProxyEffectFactory {

    override val proxyConfiguration: ProxyConfiguration = RuntimeProxyConfiguration

    override fun findTargetInterfaces(clazz: KClass<*>): Set<KClass<*>> {
        if (clazz.java.isInterface) return setOf(clazz)
        val interfaces = getTargetInterfacesFromAnnotation(clazz)
            ?: takeAllSuperinterfaces(clazz)
        if (interfaces.isEmpty()) {
            throw EffectNotFoundException(clazz, proxyConfiguration)
        }
        return interfaces
    }

    override fun <T : Any> createProxy(
        clazz: KClass<T>,
        commandExecutor: CommandExecutor<T>,
    ): T {
        val javaInterface = clazz.java
        val proxy = Proxy.newProxyInstance(
            javaInterface.classLoader,
            arrayOf(javaInterface, AutoCloseable::class.java),
            ProxyMethodInterceptor(commandExecutor)
        )
        return proxy as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun getTargetInterfacesFromAnnotation(clazz: KClass<*>): Set<KClass<*>>? {
        clazz.java.annotations.forEach { annotation ->
            try {
                val targetMethod = annotation.javaClass.getDeclaredMethod(TARGET_ANNOTATION_ARG)
                val targetClasses = targetMethod.invoke(annotation) as Array<Class<*>>
                if (targetClasses.isEmpty()) return null
                return targetClasses.map { it.kotlin }.toSet()
            } catch (ignored: NoSuchMethodException) { }
        }
        return null
    }

    private fun takeAllSuperinterfaces(clazz: KClass<*>): Set<KClass<*>> {
        return clazz.java.interfaces.map { it.kotlin }.toSet()
    }

    private companion object {
        const val TARGET_ANNOTATION_ARG = "targets"
    }

}

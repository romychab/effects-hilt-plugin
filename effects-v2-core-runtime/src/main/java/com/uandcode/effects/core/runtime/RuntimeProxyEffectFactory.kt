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

    override fun findTargetInterface(clazz: KClass<*>): KClass<*> {
        if (clazz.java.isInterface) return clazz
        return getTargetInterfaceFromAnnotation(clazz)
            ?: takeFirstSuperinterface(clazz)
            ?: throw EffectNotFoundException(clazz, proxyConfiguration)
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

    private fun getTargetInterfaceFromAnnotation(clazz: KClass<*>): KClass<*>? {
        clazz.java.annotations.forEach { annotation ->
            try {
                val targetMethod = annotation.javaClass.getDeclaredMethod(TARGET_ANNOTATION_ARG)
                val targetClass = targetMethod.invoke(annotation) as Class<*>
                if (targetClass != Any::class.java) {
                    return targetClass.kotlin
                }
            } catch (ignored: NoSuchMethodException) { }
        }
        return null
    }

    private fun takeFirstSuperinterface(clazz: KClass<*>): KClass<*>? {
        return clazz.java.interfaces.firstOrNull()?.kotlin
    }

    private companion object {
        const val TARGET_ANNOTATION_ARG = "target"
    }

}

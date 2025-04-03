package com.uandcode.effects.core.runtime

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.ProxyEffectFactory
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.stub.api.ProxyConfiguration
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import kotlin.reflect.KClass

/**
 * A [ProxyEffectFactory] implementation that uses ByteBuddy to generate
 * and load proxy implementations at runtime.
 *
 * Usually you don't need to use this class directly. See [RuntimeEffectComponents] instead.
 */
public class RuntimeProxyEffectFactory : ProxyEffectFactory {

    private val proxyClasses = mutableMapOf<KClass<*>, Class<*>>()

    override val proxyConfiguration: ProxyConfiguration = RuntimeProxyConfiguration

    override fun findTargetInterface(clazz: KClass<*>): KClass<*> {
        if (clazz.java.isInterface) return clazz
        return findInterfaceInMetadata(clazz)
            ?: takeFirstSuperinterface(clazz)
            ?: throw EffectNotFoundException(clazz, proxyConfiguration)
    }

    override fun <T : Any> createProxy(
        clazz: KClass<T>,
        commandExecutor: CommandExecutor<T>,
    ): T {
        val proxyClass = proxyClasses.getOrPut(clazz) {
            createProxyClass(clazz, commandExecutor)
        }
        return proxyClass
            .getDeclaredConstructor()
            .newInstance() as T
    }

    private fun <T : Any> createProxyClass(
        clazz: KClass<T>,
        commandExecutor: CommandExecutor<T>,
    ): Class<out T> {
        val javaInterface = clazz.java
        val proxyMethodInterceptor = ProxyMethodInterceptor(commandExecutor)
        return ByteBuddy()
            .subclass(javaInterface)
            .method(ElementMatchers.isDeclaredBy(javaInterface))
            .intercept(MethodDelegation.to(proxyMethodInterceptor))
            .implement(AutoCloseable::class.java)
            .intercept(MethodDelegation.to(proxyMethodInterceptor, "close"))
            .make()
            .load(javaInterface.classLoader)
            .loaded
    }

    private fun findInterfaceInMetadata(clazz: KClass<*>): KClass<*>? {
        clazz.java.annotations.forEach { annotation ->
            try {
                val targetMethod = annotation.javaClass.getDeclaredMethod("target")
                val targetClass = targetMethod.invoke(annotation) as Class<*>
                if (targetClass != Any::class.java) {
                    return clazz.java.interfaces.firstOrNull()?.kotlin
                }
            } catch (ignored: NoSuchMethodException) { }
        }
        return null
    }

    private fun takeFirstSuperinterface(clazz: KClass<*>): KClass<*>? {
        return clazz.java.interfaces.firstOrNull()?.kotlin
    }

}

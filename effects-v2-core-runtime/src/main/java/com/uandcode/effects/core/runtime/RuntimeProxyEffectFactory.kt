package com.uandcode.effects.core.runtime

import com.uandcode.effects.core.CommandExecutor
import com.uandcode.effects.core.ProxyEffectFactory
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.runtime.proxy.ProxyMethodInterceptor
import com.uandcode.effects.stub.api.ProxyConfiguration
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType

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
        return getTargetInterfaceFromAnnotation(clazz)
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
        val cleanUpMethodName = findCleanUpMethodName(clazz)
        val javaInterface = clazz.java
        val proxyMethodInterceptor = ProxyMethodInterceptor(commandExecutor, cleanUpMethodName)
        return ByteBuddy()
            .subclass(javaInterface)
            .method(ElementMatchers.isDeclaredBy(javaInterface))
            .intercept(MethodDelegation.to(proxyMethodInterceptor))
            .implement(AutoCloseable::class.java)
            .intercept(MethodDelegation.to(proxyMethodInterceptor, DEFAULT_CLOSE_NAME))
            .make()
            .load(javaInterface.classLoader)
            .loaded
    }

    private fun getTargetInterfaceFromAnnotation(clazz: KClass<*>): KClass<*>? {
        clazz.java.annotations.forEach { annotation ->
            try {
                val targetMethod = annotation.javaClass.getDeclaredMethod(TARGET_ANNOTATION_ARG)
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

    private fun findCleanUpMethodName(clazz: KClass<*>): String? {
        return clazz.members.firstOrNull { callable ->
            !callable.isAbstract
                    && callable.returnType == UNIT_TYPE
                    && callable.parameters.count { it.kind != KParameter.Kind.INSTANCE } == 0
        }?.name
    }

    private companion object {
        const val DEFAULT_CLOSE_NAME = "close"
        const val TARGET_ANNOTATION_ARG = "target"
        val UNIT_TYPE = Unit::class.createType()
    }

}

package com.uandcode.example.core.multimodule.effects.api.locator

import kotlin.reflect.KClass

public interface Locator {
    public fun <T : Any> get(clazz: KClass<T>): T

    public interface Builder {
        public fun <T : Any> singleton(clazz: KClass<T>, provider: Locator.() -> T)
        public fun <T : Any> factory(clazz: KClass<T>, provider: Locator.() -> T)
    }

    public companion object : Locator {
        private lateinit var instance: Locator

        override fun <T : Any> get(clazz: KClass<T>): T {
            return instance.get(clazz)
        }

        public fun setup(builder: Builder.() -> Unit) {
            instance = LocatorImpl().apply(builder)
        }
    }
}

public inline fun <reified T : Any> Locator.get(): T = get(T::class)

public inline fun <reified T : Any> Locator.Builder.singleton(noinline provider: Locator.() -> T): Unit {
    singleton(T::class, provider)
}

public inline fun <reified T : Any> Locator.Builder.factory(noinline provider: Locator.() -> T): Unit {
    factory(T::class, provider)
}

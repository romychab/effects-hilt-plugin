package com.uandcode.example.core.multimodule.effects.api.locator

import kotlin.reflect.KClass

internal class LocatorImpl : Locator, Locator.Builder {

    private val map = mutableMapOf<KClass<*>, (Locator) -> Any>()

    override fun <T : Any> get(clazz: KClass<T>): T {
        return checkNotNull(map[clazz]).invoke(this) as T
    }

    override fun <T : Any> singleton(clazz: KClass<T>, provider: Locator.() -> T) {
        var instance: T? = null
        map[clazz] = { locator ->
            instance ?: provider(locator).also { instance = it }
        }
    }

    override fun <T : Any> factory(clazz: KClass<T>, provider: Locator.() -> T) {
        map[clazz] = provider
    }

}

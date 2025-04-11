package com.uandcode.effects.core.internal.scopes

import com.uandcode.effects.core.internal.ObservableResourceStore
import kotlin.reflect.KClass

internal interface HasManagedResourceStores {
    fun getResourceStore(interfaceClass: KClass<*>): ObservableResourceStore<*>?
}
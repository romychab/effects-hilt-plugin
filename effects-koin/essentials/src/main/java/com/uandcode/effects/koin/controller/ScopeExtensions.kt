package com.uandcode.effects.koin.controller

import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.bind
import com.uandcode.effects.core.getController
import com.uandcode.effects.koin.annotations.KoinEffect
import org.koin.core.scope.Scope

/**
 * Get an [EffectController] instance to detach/attach an effect
 * implementation to the target effect interface. The type parameter [T] can
 * be either an effect interface or a subclass annotated with [KoinEffect].
 */
public inline fun <reified T : Any> Scope.getEffectController(): EffectController<T> {
    return get<EffectScope>().getController<T>()
}

/**
 * Lazily inject an [EffectController] instance to detach/attach an effect
 * implementation to the target effect interface. The type parameter [T] can
 * be either an effect interface or a subclass annotated with [KoinEffect].
 */
public inline fun <reified T : Any> Scope.injectEffectController(): Lazy<EffectController<T>> {
    return lazy { getEffectController() }
}

/**
 * Get a [BoundEffectController] instance to detach/attach an effect
 * implementation to the target effect interface. The effect implementation
 * is created by the [provider], which is called lazily and only once either
 * upon the first access, or upon [BoundEffectController.start] call.
 * The type parameter [T] can be either an effect interface or a subclass
 * annotated with [KoinEffect].
 */
public inline fun <reified T : Any> Scope.getBoundEffectController(
    noinline provider: () -> T,
): BoundEffectController<T> {
    return getEffectController<T>().bind(provider)
}

/**
 * Lazily inject a [BoundEffectController] instance to detach/attach an effect
 * implementation to the target effect interface. The effect implementation
 * is created by the [provider], which is called lazily and only once either
 * upon the first access, or upon [BoundEffectController.start] call.
 * The type parameter [T] can be either an effect interface or a subclass
 * annotated with [KoinEffect].
 */
public inline fun <reified T : Any> Scope.injectBoundEffectController(
    noinline provider: () -> T,
): Lazy<BoundEffectController<T>> {
    return lazy { getBoundEffectController(provider) }
}

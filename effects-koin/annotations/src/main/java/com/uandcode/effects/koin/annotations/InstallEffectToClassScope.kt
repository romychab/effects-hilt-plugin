package com.uandcode.effects.koin.annotations

import kotlin.reflect.KClass

/**
 * Assign all target interfaces implemented by a class annotated with [KoinEffect]
 * annotation to the specified Koin scope.
 *
 * This annotation must be used in combination with [KoinEffect].
 */
public annotation class InstallEffectToClassScope(
    val value: KClass<*>
)

package com.uandcode.effects.koin.annotations

/**
 * Assign all target interfaces implemented by a class annotated with [KoinEffect]
 * annotation to the specified Koin scope.
 *
 * This annotation must be used in combination with [KoinEffect].
 */
public annotation class InstallEffectToNamedScope(
    val value: String
)

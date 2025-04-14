package com.uandcode.effects.koin.exceptions

import org.koin.core.qualifier.Qualifier

/**
 * This exception is thrown when the validation logic finds
 * a duplicated effectScope { ... } definition in your Koin modules.
 */
public class DuplicateEffectScopeException(
    duplicatedDefinition: Qualifier
) : Exception(
    "Found duplicated effectScope() definition: $duplicatedDefinition"
)

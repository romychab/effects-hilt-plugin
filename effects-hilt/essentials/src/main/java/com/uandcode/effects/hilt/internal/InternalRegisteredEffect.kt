package com.uandcode.effects.hilt.internal

import com.uandcode.effects.hilt.internal.qualifiers.AbstractInternalQualifier
import kotlin.reflect.KClass

public data class InternalRegisteredEffect(
    public val clazz: KClass<*>,
    public val qualifier: KClass<out AbstractInternalQualifier>,
)

public fun Set<InternalRegisteredEffect>.filterByQualifier(
    qualifier: KClass<out AbstractInternalQualifier>
): Array<KClass<*>> {
    return this.filter { it.qualifier == qualifier }
        .map { it.clazz }
        .toTypedArray()
}

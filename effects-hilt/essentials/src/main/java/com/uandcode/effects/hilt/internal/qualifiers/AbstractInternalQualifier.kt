package com.uandcode.effects.hilt.internal.qualifiers

import com.uandcode.effects.core.EffectScope
import kotlin.reflect.KClass

public sealed class AbstractInternalQualifier(
    public val scope: EffectScope
) {
    public abstract val priority: Int

    public companion object {
        public val singleton: KClass<out AbstractInternalQualifier> = SingletonQualifier::class
        public val activityRetained: KClass<out AbstractInternalQualifier> = ActivityRetainedQualifier::class
        public val viewModel: KClass<out AbstractInternalQualifier> = ViewModelQualifier::class
        public val activity: KClass<out AbstractInternalQualifier> = ActivityQualifier::class
        public val fragment: KClass<out AbstractInternalQualifier> = FragmentQualifier::class
    }
}

internal fun Set<AbstractInternalQualifier>.getEffectScopeWithMaxPriority(): EffectScope {
    return maxBy { it.priority }.scope
}

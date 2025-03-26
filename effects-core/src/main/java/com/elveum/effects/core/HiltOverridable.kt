package com.elveum.effects.core

public class HiltOverridable<T>(
    internal val instance: T,
    internal val priority: Int,
) {
    public companion object {
        public const val SINGLETON_PRIORITY: Int = 0
        public const val ACTIVITY_RETAINED_PRIORITY: Int = 1
        public const val VIEW_MODEL_PRIORITY: Int = 2
        public const val OTHER_PRIORITY: Int = 999
    }
}

public fun <T> Set<HiltOverridable<T>>.getInstanceWithMaxPriority(): T {
    val overridableWithMaxPriority = checkNotNull(
        this.maxByOrNull { it.priority }
    )
    return overridableWithMaxPriority.instance
}

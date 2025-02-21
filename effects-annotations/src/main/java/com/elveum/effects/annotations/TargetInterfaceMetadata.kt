package com.elveum.effects.annotations

/**
 * For internal usage only.
 *
 * This annotation is used by KSP code generator in order to collect
 * effect interfaces from all modules.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public annotation class TargetInterfaceMetadata(
    val implementationClassName: String,
    val interfaceClassName: String,
    val hiltComponentClassName: String,
    val hiltScopeClassName: String,
)

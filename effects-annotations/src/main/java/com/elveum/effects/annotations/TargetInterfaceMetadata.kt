package com.elveum.effects.annotations

/**
 * For internal usage only.
 *
 * This annotation is used by KSP code generator for correct collecting
 * of effect interfaces in multi-module projects.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
public annotation class TargetInterfaceMetadata(
    val implementationClassName: String,
    val interfaceClassNames: Array<String>,
    val hiltComponentClassName: String,
    val hiltScopeClassName: String,
    val cleanUpMethodName: String,
)

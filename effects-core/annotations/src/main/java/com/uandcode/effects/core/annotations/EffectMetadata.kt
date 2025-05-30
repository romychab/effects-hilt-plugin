package com.uandcode.effects.core.annotations

/**
 * For internal usage.
 *
 * This annotation is used by KSP code generator for correct collecting
 * of effect interfaces in multi-module projects.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
public annotation class EffectMetadata(
    val interfaceClassNames: Array<String>,
    val implementationClassName: String,
)

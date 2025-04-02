package com.uandcode.effects.stub.api

/**
 * For internal usage.
 *
 * Defines the names of annotations which will appear in exceptions thrown
 * by the library. This configuration can be overridden by various
 * library extensions which can use different annotations.
 */
public data class ProxyConfiguration(
    val applicationAnnotationName: String = "@EffectApplication",
    val effectAnnotationName: String = "@EffectClass",
)

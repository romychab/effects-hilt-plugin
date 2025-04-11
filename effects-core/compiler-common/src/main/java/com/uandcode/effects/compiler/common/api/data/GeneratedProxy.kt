package com.uandcode.effects.compiler.common.api.data

import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.ClassName

/**
 * Information about a generated proxy class.
 * - [proxyClassName] - full name of the generated proxy class.
 * - [interfaceClassName] - the original interface for which the proxy class was generated.
 * - [dependencies] - list of files that were used upon the generation of the proxy class.
 */
public data class GeneratedProxy(
    val proxyClassName: ClassName,
    val interfaceClassName: ClassName,
    override val dependencies: Dependencies,
) : HasDependencies

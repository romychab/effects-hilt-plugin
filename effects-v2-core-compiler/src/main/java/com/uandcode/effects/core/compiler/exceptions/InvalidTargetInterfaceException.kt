package com.uandcode.effects.core.compiler.exceptions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.core.compiler.api.AbstractEffectKspException
import com.uandcode.effects.core.compiler.api.extensions.KSAnnotationWrapper

internal class InvalidTargetInterfaceException(
    allowedInterfaces: List<KSClassDeclaration>,
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : AbstractEffectKspException(
    message = "@${effectAnnotation.simpleName}(target = ...) parameter can be set only to these values: " +
            allowedInterfaces.joinToString(", ") { "${it.toClassName().simpleName}::class" } ,
    node,
)

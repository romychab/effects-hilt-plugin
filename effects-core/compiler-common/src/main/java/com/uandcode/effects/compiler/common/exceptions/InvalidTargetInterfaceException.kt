package com.uandcode.effects.compiler.common.exceptions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.compiler.common.api.AbstractEffectKspException
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper

internal class InvalidTargetInterfaceException(
    allowedInterfaces: List<KSClassDeclaration>,
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : AbstractEffectKspException(
    message = "@${effectAnnotation.simpleName}(targets = ...) parameter can be set only to these values: " +
            allowedInterfaces.joinToString(", ") { "${it.toClassName().simpleName}::class" } ,
    node,
)

package com.elveum.effects.processor.exceptions

import com.elveum.effects.processor.extensions.KSAnnotationWrapper
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ksp.toClassName

class InvalidTargetInterfaceException(
    allowedInterfaces: List<KSClassDeclaration>,
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : AbstractEffectKspException(
    message = "@${effectAnnotation.simpleName}(target = ...) parameter can be set only to these values: " +
            allowedInterfaces.joinToString(", ") { "${it.toClassName().simpleName}::class" } ,
    node,
)

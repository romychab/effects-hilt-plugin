package com.uandcode.effects.hilt.compiler.exceptions

import com.uandcode.effects.compiler.common.api.AbstractEffectKspException
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.hilt.compiler.data.SupportedHiltComponent

class InvalidHiltComponentException(
    effectAnnotation: KSAnnotationWrapper,
) : AbstractEffectKspException(
    "Invalid Hilt Component is specified in ${effectAnnotation.printableName}(installIn = ...). " +
    "Example of valid Hilt components: ${validComponents()}",
    effectAnnotation,
)

private fun validComponents() = SupportedHiltComponent.all.joinToString {
    "${it.simpleName}::class"
}

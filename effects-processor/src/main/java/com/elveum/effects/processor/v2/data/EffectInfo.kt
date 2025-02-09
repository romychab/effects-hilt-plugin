package com.elveum.effects.processor.v2.data

import com.elveum.effects.annotations.CustomEffect
import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper
import com.elveum.effects.processor.v2.extensions.firstInstanceOf
import com.elveum.effects.processor.v2.parser.findTargetInterfaceClassDeclaration
import com.google.devtools.ksp.symbol.KSClassDeclaration

class EffectInfo(
    val effectClassDeclaration: KSClassDeclaration
) {

    val pkg: String get() = effectClassDeclaration.packageName.asString()
    val effectAnnotation: KSAnnotationWrapper by lazy { findCustomAnnotation() }
    val targetInterface: KSClassDeclaration by lazy { findTargetInterfaceClassDeclaration() }

    private fun findCustomAnnotation(): KSAnnotationWrapper {
        return effectClassDeclaration.annotations
            .map(::KSAnnotationWrapper)
            .firstInstanceOf<CustomEffect>()
    }

}

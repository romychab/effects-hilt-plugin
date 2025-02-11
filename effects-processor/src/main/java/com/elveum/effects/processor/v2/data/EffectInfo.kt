package com.elveum.effects.processor.v2.data

import com.elveum.effects.annotations.CustomEffect
import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper
import com.elveum.effects.processor.v2.extensions.KSClassDeclarationWrapper
import com.elveum.effects.processor.v2.extensions.firstInstanceOf
import com.elveum.effects.processor.v2.parser.findTargetInterfaceClassDeclaration
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration

class EffectInfo(
    originEffectClassDeclaration: KSClassDeclaration
) {

    val effectClassDeclaration: KSClassDeclarationWrapper = KSClassDeclarationWrapper(originEffectClassDeclaration)

    val pkg: String get() = effectClassDeclaration.packageName.asString()
    val effectAnnotation: KSAnnotationWrapper by lazy { findCustomAnnotation() }
    val targetInterface: KSClassDeclarationWrapper by lazy { findTargetInterfaceClassDeclaration() }
    val targetInterfaceName: String get() = targetInterface.simpleName.asString()
    val dependencies: Dependencies by lazy { createDependencies() }

    private fun findCustomAnnotation(): KSAnnotationWrapper {
        return effectClassDeclaration.annotations
            .map(::KSAnnotationWrapper)
            .firstInstanceOf<CustomEffect>()
    }

    private fun createDependencies(): Dependencies {
        val implementationFile = effectClassDeclaration.containingFile
        val interfaceFile = targetInterface.containingFile
        checkNotNull(implementationFile)
        checkNotNull(interfaceFile)
        return Dependencies(
            aggregating = false,
            implementationFile,
            interfaceFile,
        )
    }

}

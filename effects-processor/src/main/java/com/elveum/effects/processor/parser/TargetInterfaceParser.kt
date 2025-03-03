package com.elveum.effects.processor.parser

import com.elveum.effects.processor.data.Const
import com.elveum.effects.processor.data.EffectInfo
import com.elveum.effects.processor.exceptions.ClassDoesNotImplementInterfaceException
import com.elveum.effects.processor.exceptions.InvalidTargetInterfaceException
import com.elveum.effects.processor.exceptions.TargetInterfaceIsNotSpecifiedException
import com.elveum.effects.processor.extensions.KSClassDeclarationWrapper
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ksp.toClassName

internal fun EffectInfo.findTargetInterfaceClassDeclaration(): KSClassDeclarationWrapper {
    val interfaces = effectClassDeclaration.interfaces

    if (interfaces.isEmpty()) {
        throw ClassDoesNotImplementInterfaceException(
            effectAnnotation,
            effectClassDeclaration,
        )
    }

    return interfaces.singleOrNull()
        ?.let(::KSClassDeclarationWrapper)
        ?: findTargetInterfaceInAnnotation(interfaces)
}

private fun EffectInfo.findTargetInterfaceInAnnotation(
    allowedInterfaces: List<KSClassDeclaration>,
): KSClassDeclarationWrapper {
    val targetInterface = effectAnnotation.getClassDeclaration(Const.TargetArgument)
    if (targetInterface.toClassName() == ANY) throw TargetInterfaceIsNotSpecifiedException(effectAnnotation)
    return if (allowedInterfaces.contains(targetInterface)) {
        KSClassDeclarationWrapper(targetInterface)
    } else {
        throw InvalidTargetInterfaceException(allowedInterfaces, effectAnnotation, effectClassDeclaration)
    }
}

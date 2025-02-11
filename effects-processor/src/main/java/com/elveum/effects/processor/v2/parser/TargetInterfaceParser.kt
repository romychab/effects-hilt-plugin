package com.elveum.effects.processor.v2.parser

import com.elveum.effects.processor.v2.ClassDoesNotImplementInterfaceException
import com.elveum.effects.processor.v2.InvalidTargetInterfaceException
import com.elveum.effects.processor.v2.data.Const
import com.elveum.effects.processor.v2.data.EffectInfo
import com.elveum.effects.processor.v2.extensions.KSClassDeclarationWrapper
import com.google.devtools.ksp.symbol.KSClassDeclaration

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
    return if (allowedInterfaces.contains(targetInterface)) {
        KSClassDeclarationWrapper(targetInterface)
    } else {
        throw InvalidTargetInterfaceException(allowedInterfaces, effectAnnotation, effectClassDeclaration)
    }
}

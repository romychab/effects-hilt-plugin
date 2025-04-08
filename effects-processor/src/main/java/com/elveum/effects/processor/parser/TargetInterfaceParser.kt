package com.elveum.effects.processor.parser

import com.elveum.effects.processor.data.Const
import com.elveum.effects.processor.data.EffectInfo
import com.elveum.effects.processor.exceptions.ClassDoesNotImplementInterfaceException
import com.elveum.effects.processor.exceptions.InvalidTargetInterfaceException
import com.elveum.effects.processor.extensions.KSClassDeclarationWrapper
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ksp.toClassName

internal fun EffectInfo.findTargetInterfaceClassDeclarations(): List<KSClassDeclarationWrapper> {
    val allowedInterfaces = effectClassDeclaration.interfaces

    val targets = effectAnnotation.getClassDeclarationList(Const.TargetArrayArgument)
    val finalTargets = targets.ifEmpty {
        val deprecatedTarget = effectAnnotation.getClassDeclaration(Const.TargetArgument)
        if (deprecatedTarget.toClassName() == ANY) {
            allowedInterfaces
        } else {
            listOf(deprecatedTarget)
        }
    }

    if (finalTargets.isEmpty()) {
        throw ClassDoesNotImplementInterfaceException(
            effectAnnotation,
            effectClassDeclaration,
        )
    }

    finalTargets.forEach { finalTarget ->
        if (!allowedInterfaces.contains(finalTarget)) {
            throw InvalidTargetInterfaceException(allowedInterfaces, effectAnnotation, effectClassDeclaration)
        }
    }
    return finalTargets.map(::KSClassDeclarationWrapper)
}

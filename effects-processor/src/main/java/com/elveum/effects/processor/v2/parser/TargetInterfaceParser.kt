package com.elveum.effects.processor.v2.parser

import com.elveum.effects.processor.v2.EffectKspException
import com.elveum.effects.processor.v2.data.Const
import com.elveum.effects.processor.v2.data.EffectInfo
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.toClassName

internal fun EffectInfo.findTargetInterfaceClassDeclaration(): KSClassDeclaration {
    val superTypes = effectClassDeclaration.superTypes
        .map { typeReference ->
            typeReference.resolve().declaration
        }
        .filterIsInstance<KSClassDeclaration>()
        .filter { classDeclaration ->
            classDeclaration.classKind == ClassKind.INTERFACE
        }
        .toList()

    if (superTypes.isEmpty()) {
        throw EffectKspException(
            "Class annotated with @${effectAnnotation.simpleName} should implement at least 1 interface",
            effectClassDeclaration,
        )
    }

    return superTypes.singleOrNull() ?: findTargetInterfaceInAnnotation(superTypes)
}

private fun EffectInfo.findTargetInterfaceInAnnotation(
    allowedInterfaces: List<KSClassDeclaration>,
): KSClassDeclaration {
    val targetInterface = effectAnnotation.getClassDeclaration(Const.TargetArgument)
    return if (allowedInterfaces.contains(targetInterface)) {
        targetInterface
    } else {
        throwInvalidTargetValueException(allowedInterfaces)
    }
}

private fun EffectInfo.throwInvalidTargetValueException(allowedInterfaces: List<KSClassDeclaration>): Nothing {
    throw EffectKspException(
        "@${effectAnnotation.simpleName}(target = ...) parameter can be set only to these values: " +
                allowedInterfaces.joinToString(", ") { "${it.toClassName().simpleName}::class" } ,
        effectClassDeclaration,
    )
}

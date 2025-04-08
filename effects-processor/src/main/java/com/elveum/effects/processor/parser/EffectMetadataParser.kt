@file:OptIn(KspExperimental::class)

package com.elveum.effects.processor.parser

import com.elveum.effects.processor.data.Const
import com.elveum.effects.processor.data.EffectCleanUpMethodName
import com.elveum.effects.processor.data.EffectMetadata
import com.elveum.effects.processor.extensions.KSAnnotationWrapper
import com.elveum.effects.processor.extensions.KSClassDeclarationWrapper
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName

fun parseMetadata(
    resolver: Resolver,
    hiltAppClassDeclaration: KSClassDeclaration,
): Sequence<EffectMetadata> {
    val metadata = resolver.getDeclarationsFromPackage(Const.MetadataPackage)
    return metadata
        .filterIsInstance<KSClassDeclaration>()
        .mapNotNull { metadataDeclaration ->
            metadataDeclaration.annotations
                .findTargetInterfaceMetadataAnnotation()
                ?.let { annotation ->
                    buildEffectMetadata(
                        metadataDeclaration, hiltAppClassDeclaration, annotation, resolver
                    )
                }
        }
}

private fun Sequence<KSAnnotation>.findTargetInterfaceMetadataAnnotation(): KSAnnotationWrapper? {
    return firstOrNull {
        it.shortName.asString() == Const.TargetInterfaceMetadataAnnotation
    }?.let(::KSAnnotationWrapper)
}

private fun buildEffectMetadata(
    metadataDeclaration: KSClassDeclaration,
    hiltAppClassDeclaration: KSClassDeclaration,
    annotation: KSAnnotationWrapper,
    resolver: Resolver,
): EffectMetadata? {
    val interfaceDeclarationNames = annotation.getStringList(Const.MetadataInterfaceClassnames)
    val interfaceDeclarations = interfaceDeclarationNames.mapNotNull { name ->
        resolver.getClassDeclarationByName(name)
    }.map(::KSClassDeclarationWrapper)
    val implDeclaration = resolver.getClassDeclarationByName(
        annotation.getString(Const.MetadataImplClassname)
    )
    val hiltComponent = ClassName.bestGuess(annotation.getString(Const.MetadataHiltComponent))
    val hiltScope = ClassName.bestGuess(annotation.getString(Const.MetadataHiltScope))
    val originCleanUpMethodName = annotation.getString(Const.MetadataCleanUpMethodName)

    return if (interfaceDeclarations.isNotEmpty() && implDeclaration != null) {
        EffectMetadata(
            targetInterfaceList = interfaceDeclarations,
            effectClassDeclaration = KSClassDeclarationWrapper(implDeclaration),
            hiltComponent = hiltComponent,
            hiltScope = hiltScope,
            metadataDeclaration = metadataDeclaration,
            hiltAppClassDeclaration = hiltAppClassDeclaration,
            cleanUpMethodName = EffectCleanUpMethodName(originCleanUpMethodName)
        )
    } else {
        null
    }
}

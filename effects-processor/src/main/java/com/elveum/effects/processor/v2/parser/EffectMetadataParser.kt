@file:OptIn(KspExperimental::class)

package com.elveum.effects.processor.v2.parser

import com.elveum.effects.processor.v2.data.Const
import com.elveum.effects.processor.v2.data.EffectMetadata
import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper
import com.elveum.effects.processor.v2.extensions.KSClassDeclarationWrapper
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName

fun parseMetadata(resolver: Resolver): Sequence<EffectMetadata> {
    val metadata = resolver.getDeclarationsFromPackage(Const.MetadataPackage)
    return metadata
        .filterIsInstance<KSClassDeclaration>()
        .mapNotNull { metadataDeclaration ->
            metadataDeclaration.annotations
                .findTargetInterfaceMetadataAnnotation()
                ?.let { buildEffectMetadata(it, resolver) }
        }
}

private fun Sequence<KSAnnotation>.findTargetInterfaceMetadataAnnotation(): KSAnnotationWrapper? {
    return firstOrNull {
        it.shortName.asString() == Const.TargetInterfaceMetadataAnnotation
    }?.let(::KSAnnotationWrapper)
}

private fun buildEffectMetadata(
    annotation: KSAnnotationWrapper,
    resolver: Resolver,
): EffectMetadata? {
    val interfaceDeclaration = resolver.getClassDeclarationByName(
        annotation.getString(Const.MetadataInterfaceClassname)
    )
    val implDeclaration = resolver.getClassDeclarationByName(
        annotation.getString(Const.MetadataImplClassname)
    )
    val hiltComponent = ClassName.bestGuess(annotation.getString(Const.MetadataHiltComponent))
    val hiltScope = ClassName.bestGuess(annotation.getString(Const.MetadataHiltScope))

    return if (interfaceDeclaration != null && implDeclaration != null) {
        EffectMetadata(
            targetInterfaceDeclaration = KSClassDeclarationWrapper(interfaceDeclaration),
            effectClassDeclaration = KSClassDeclarationWrapper(implDeclaration),
            hiltComponent = hiltComponent,
            hiltScope = hiltScope
        )
    } else {
        null
    }
}

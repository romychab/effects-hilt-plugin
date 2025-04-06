@file:OptIn(KspExperimental::class)

package com.uandcode.effects.core.compiler.parser

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.uandcode.effects.core.compiler.Const
import com.uandcode.effects.core.compiler.api.EffectExtension
import com.uandcode.effects.core.compiler.api.data.ParsedMetadata
import com.uandcode.effects.core.compiler.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.core.compiler.api.extensions.KSClassDeclarationWrapper

internal fun parseMetadata(
    resolver: Resolver,
    extension: EffectExtension,
    applicationClassDeclaration: KSClassDeclaration,
): List<ParsedMetadata> {
    val metadata = resolver.getDeclarationsFromPackage(Const.MetadataPackage)
    return metadata
        .filterIsInstance<KSClassDeclaration>()
        .mapNotNull { metadataDeclaration ->
            metadataDeclaration.annotations
                .findMetadataAnnotation(extension)
                ?.let { annotation ->
                    buildParsedMetadata(
                        metadataDeclaration, applicationClassDeclaration, annotation, resolver, extension,
                    )
                }
        }
        .toList()
}

private fun Sequence<KSAnnotation>.findMetadataAnnotation(
    effectExtension: EffectExtension,
): KSAnnotationWrapper? {
    return firstOrNull {
        it.shortName.asString() == effectExtension.metadataAnnotation.simpleName
    }?.let(::KSAnnotationWrapper)
}

private fun buildParsedMetadata(
    metadataDeclaration: KSClassDeclaration,
    applicationClassDeclaration: KSClassDeclaration,
    annotation: KSAnnotationWrapper,
    resolver: Resolver,
    extension: EffectExtension,
): ParsedMetadata? {
    val interfaceDeclaration = resolver.getClassDeclarationByName(
        annotation.getString(Const.MetadataInterfaceClassName)
    )
    val implDeclaration = resolver.getClassDeclarationByName(
        annotation.getString(Const.MetadataImplementationClassName)
    )

    return if (interfaceDeclaration != null && implDeclaration != null) {
        extension.buildMetadataFromAnnotation(
            applicationClassDeclaration = applicationClassDeclaration,
            interfaceDeclaration = KSClassDeclarationWrapper(interfaceDeclaration),
            implementationClassDeclaration = KSClassDeclarationWrapper(implDeclaration),
            metadataAnnotation = annotation,
            metadataDeclaration = metadataDeclaration,
        )
    } else {
        null
    }
}

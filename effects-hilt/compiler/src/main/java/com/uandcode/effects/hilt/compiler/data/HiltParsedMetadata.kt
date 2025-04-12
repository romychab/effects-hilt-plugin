package com.uandcode.effects.hilt.compiler.data

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.uandcode.effects.compiler.common.api.data.ParsedMetadata
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper

class HiltParsedMetadata(
    interfaceDeclarations: List<KSClassDeclarationWrapper>,
    implementationClassDeclaration: KSClassDeclarationWrapper,
    val hiltComponent: SupportedHiltComponent,
    metadataDeclaration: KSClassDeclaration? = null,
) : ParsedMetadata(
    interfaceDeclarations,
    implementationClassDeclaration,
    metadataDeclaration,
) {

    constructor(
        effect: HiltParsedEffect,
    ) : this(
        interfaceDeclarations = effect.targetInterfaces,
        implementationClassDeclaration = effect.classDeclaration,
        hiltComponent = effect.hiltComponent,
    )

}

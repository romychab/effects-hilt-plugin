package com.uandcode.effects.koin.compiler.data

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.uandcode.effects.compiler.common.api.data.ParsedMetadata
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper

class KoinParsedMetadata(
    val koinScope: KoinScope,
    interfaceDeclarations: List<KSClassDeclarationWrapper>,
    implementationClassDeclaration: KSClassDeclarationWrapper,
    metadataDeclaration: KSClassDeclaration? = null,
) : ParsedMetadata(
    interfaceDeclarations, implementationClassDeclaration, metadataDeclaration
) {

    constructor(
        effect: KoinParsedEffect
    ) : this(
        koinScope = effect.koinScope,
        interfaceDeclarations = effect.targetInterfaces,
        implementationClassDeclaration = effect.classDeclaration,
    )

}

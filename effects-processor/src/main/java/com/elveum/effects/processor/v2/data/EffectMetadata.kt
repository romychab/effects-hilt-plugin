package com.elveum.effects.processor.v2.data

import com.elveum.effects.processor.v2.extensions.KSClassDeclarationWrapper
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

class EffectMetadata(
    val targetInterfaceDeclaration: KSClassDeclarationWrapper,
    val effectClassDeclaration: KSClassDeclarationWrapper,
    val hiltComponent: ClassName,
    val hiltScope: ClassName,
    val hiltAppClassDeclaration: KSClassDeclaration,
    val metadataDeclaration: KSClassDeclaration? = null,
) {

    val effectClassName: ClassName = effectClassDeclaration.toClassName()
    val targetInterfaceClassName: ClassName by lazy { targetInterfaceDeclaration.toClassName() }
    val targetInterfaceName: String get() = targetInterfaceClassName.simpleName
    val pkg: String = targetInterfaceDeclaration.packageName.asString()
    val dependencies: Dependencies by lazy { buildDependencies() }

    constructor(
        effectInfo: EffectInfo,
        hiltAppClassDeclaration: KSClassDeclaration,
    ) : this(
        targetInterfaceDeclaration = effectInfo.targetInterface,
        effectClassDeclaration = effectInfo.effectClassDeclaration,
        hiltComponent = effectInfo.hiltComponent,
        hiltScope = effectInfo.hiltScope,
        hiltAppClassDeclaration = hiltAppClassDeclaration,
    )

    private fun buildDependencies(): Dependencies {
        val files = listOfNotNull(
            targetInterfaceDeclaration.containingFile,
            effectClassDeclaration.containingFile,
            metadataDeclaration?.containingFile,
            hiltAppClassDeclaration.containingFile,
        ).toTypedArray()
        return Dependencies(
            aggregating = false,
            *files,
        )
    }

}
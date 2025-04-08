package com.elveum.effects.processor.data

import com.elveum.effects.processor.extensions.KSClassDeclarationWrapper
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

class EffectMetadata(
    val targetInterfaceList: List<KSClassDeclarationWrapper>,
    val effectClassDeclaration: KSClassDeclarationWrapper,
    val hiltComponent: ClassName,
    val hiltScope: ClassName,
    val hiltAppClassDeclaration: KSClassDeclaration,
    val cleanUpMethodName: EffectCleanUpMethodName,
    val metadataDeclaration: KSClassDeclaration? = null,
) {

    val effectClassName: ClassName = effectClassDeclaration.toClassName()
    val dependencies: Dependencies by lazy { buildDependencies() }

    private val hiltComponentToConstNameMap = mapOf(
        Const.SingletonComponentName to Const.SingletonPriorityConstName,
        Const.ActivityRetainedComponentName to Const.ActivityRetainedPriorityConstName,
    )

    constructor(
        effectInfo: EffectInfo,
        hiltAppClassDeclaration: KSClassDeclaration,
    ) : this(
        targetInterfaceList = effectInfo.targetInterfaceList,
        effectClassDeclaration = effectInfo.effectClassDeclaration,
        hiltComponent = effectInfo.hiltComponent,
        hiltScope = effectInfo.hiltScope,
        cleanUpMethodName = effectInfo.cleanUpMethodName,
        hiltAppClassDeclaration = hiltAppClassDeclaration,
    )

    fun shouldGenerateViewModelMediator(): Boolean {
        return hiltComponentToConstNameMap.keys.contains(hiltComponent)
    }

    fun getHiltComponentPriorityConstName(): String {
        return hiltComponentToConstNameMap[hiltComponent]
            ?: Const.OtherPriorityConstName
    }

    private fun buildDependencies(): Dependencies {
        val interfaceFiles = targetInterfaceList.mapNotNull { it.containingFile }
        val files = interfaceFiles + listOfNotNull(
            effectClassDeclaration.containingFile,
            metadataDeclaration?.containingFile,
            hiltAppClassDeclaration.containingFile,
        ).toTypedArray()
        return Dependencies(
            aggregating = false,
            *files.toTypedArray(),
        )
    }

}
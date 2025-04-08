package com.elveum.effects.processor.validators

import com.elveum.effects.processor.data.EffectMetadata
import com.elveum.effects.processor.exceptions.InconsistentCleanUpMethodNameException
import com.elveum.effects.processor.exceptions.InconsistentHiltComponentsException
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

fun validateAndFilterEffectMetadata(effectMetadata: Sequence<EffectMetadata>): Map<ClassName, List<EffectMetadata>> {
    val interfaceToImplementationsMap = mutableMapOf<ClassName, MutableList<EffectMetadata>>()
    effectMetadata.forEach { metadata ->
        metadata.targetInterfaceList.forEach { interfaceDeclaration ->
            val interfaceClassName = interfaceDeclaration.toClassName()
            val list = interfaceToImplementationsMap.computeIfAbsent(interfaceClassName) { mutableListOf() }
            list.add(metadata)
        }
    }
    interfaceToImplementationsMap.mapValues { entry ->
        entry.value.distinctBy { it.effectClassName }
    }

    interfaceToImplementationsMap.forEach {  (interfaceName, effectMetadataList) ->
        val firstMetadata = effectMetadataList.first()
        effectMetadataList.forEach { metadata ->
            if (metadata.hiltScope != firstMetadata.hiltScope ||
                metadata.hiltComponent != firstMetadata.hiltComponent) {
                throw InconsistentHiltComponentsException(
                    firstMetadata.targetInterfaceList.first { it.toClassName() == interfaceName },
                    firstMetadata.effectClassName,
                    firstMetadata.hiltComponent,
                    metadata.effectClassName,
                    metadata.hiltComponent,
                )
            }
            if (metadata.cleanUpMethodName.simpleText != firstMetadata.cleanUpMethodName.simpleText) {
                throw InconsistentCleanUpMethodNameException(
                    firstMetadata.targetInterfaceList.first { it.toClassName() == interfaceName },
                    firstMetadata.effectClassName,
                    firstMetadata.cleanUpMethodName.simpleText,
                    metadata.effectClassName,
                    metadata.cleanUpMethodName.simpleText,
                )
            }
        }
    }

    return interfaceToImplementationsMap
}

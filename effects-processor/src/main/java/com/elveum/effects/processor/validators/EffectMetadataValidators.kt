package com.elveum.effects.processor.validators

import com.elveum.effects.processor.data.EffectMetadata
import com.elveum.effects.processor.exceptions.InconsistentCleanUpMethodNameException
import com.elveum.effects.processor.exceptions.InconsistentHiltComponentsException

fun validateAndFilterEffectMetadata(effectMetadata: Sequence<EffectMetadata>): List<EffectMetadata> {
    val effectMetadataMap = effectMetadata
        .groupBy { it.targetInterfaceClassName }
        .mapValues { (_, list) ->
            list.distinctBy { it.effectClassName }
        }

    effectMetadataMap.forEach {  (_, effectMetadataList) ->
        val firstEffect = effectMetadataList.first()
        effectMetadataList.forEach {
            if (it.hiltScope != firstEffect.hiltScope ||
                it.hiltComponent != firstEffect.hiltComponent) {
                throw InconsistentHiltComponentsException(
                    firstEffect.targetInterfaceDeclaration,
                    firstEffect.effectClassName,
                    firstEffect.hiltComponent,
                    it.effectClassName,
                    it.hiltComponent,
                )
            }
            if (it.cleanUpMethodName.simpleText != firstEffect.cleanUpMethodName.simpleText) {
                throw InconsistentCleanUpMethodNameException(
                    firstEffect.targetInterfaceDeclaration,
                    firstEffect.effectClassName,
                    firstEffect.cleanUpMethodName.simpleText,
                    it.effectClassName,
                    it.cleanUpMethodName.simpleText,
                )
            }
        }
    }

    return effectMetadataMap.map {
        it.value.first()
    }

}

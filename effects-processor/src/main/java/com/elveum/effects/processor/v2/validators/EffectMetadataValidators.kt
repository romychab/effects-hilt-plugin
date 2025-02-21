package com.elveum.effects.processor.v2.validators

import com.elveum.effects.processor.v2.data.EffectMetadata
import com.elveum.effects.processor.v2.exceptions.InconsistentHiltComponentsException

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
        }
    }

    return effectMetadataMap.map {
        it.value.first()
    }

}

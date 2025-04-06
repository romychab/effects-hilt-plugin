package com.uandcode.effects.core.compiler.validators

import com.squareup.kotlinpoet.ClassName
import com.uandcode.effects.core.compiler.api.EffectExtension
import com.uandcode.effects.core.compiler.api.data.ParsedMetadata

internal fun validateAndFilterEffectMetadata(
    effectExtension: EffectExtension,
    metadataList: List<ParsedMetadata>,
): MetadataValidationResult {
    val effectMetadataMap = metadataList
        .groupBy { it.interfaceClassName }
        .mapValues { (_, list) ->
            list.distinctBy { it.implementationClassName }
        }

    effectExtension.validateMetadata(effectMetadataMap)

    val uniqueMetadata = effectMetadataMap.map {
        it.value.first()
    }
    return MetadataValidationResult(
        uniqueMetadata = uniqueMetadata,
        groupedMetadata = effectMetadataMap,
    )
}

internal class MetadataValidationResult(
    val uniqueMetadata: List<ParsedMetadata>,
    val groupedMetadata: Map<ClassName, List<ParsedMetadata>>,
)

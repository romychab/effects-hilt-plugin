package com.uandcode.effects.compiler.common.validators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.compiler.common.api.EffectExtension
import com.uandcode.effects.compiler.common.api.data.ParsedMetadata
import com.uandcode.effects.compiler.common.api.data.GroupedMetadata

internal fun validateAndFilterEffectMetadata(
    effectExtension: EffectExtension,
    metadataList: List<ParsedMetadata>,
): GroupedMetadata {
    val interfaceToImplementationsMap = mutableMapOf<ClassName, MutableList<ParsedMetadata>>()
    metadataList.forEach { metadata ->
        metadata.interfaceDeclarations.forEach { interfaceDeclaration ->
            val interfaceClassName = interfaceDeclaration.toClassName()
            val list = interfaceToImplementationsMap.computeIfAbsent(interfaceClassName) { mutableListOf() }
            list.add(metadata)
        }
    }
    interfaceToImplementationsMap.mapValues { entry ->
        entry.value.distinctBy { it.implementationClassName }
    }

    effectExtension.validateMetadata(interfaceToImplementationsMap)

    return GroupedMetadata(
        groupedMetadata = interfaceToImplementationsMap,
    )
}


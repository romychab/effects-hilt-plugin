package com.uandcode.effects.core.compiler.validators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.core.compiler.api.EffectExtension
import com.uandcode.effects.core.compiler.api.data.ParsedMetadata
import com.uandcode.effects.core.compiler.api.data.GroupedMetadata

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


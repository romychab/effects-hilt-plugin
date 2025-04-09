package com.uandcode.effects.core.compiler.api.data

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.core.compiler.api.extensions.KSClassDeclarationWrapper

public class GroupedMetadata(
    public val groupedMetadata: Map<ClassName, List<ParsedMetadata>>,
) {

    /**
     * Set of unique pairs of effect-implementations to effect-interfaces.
     */
    public val effectPairs: Set<EffectPair> by lazy {
        groupedMetadata.values
            .flatten()
            .flatMap { parsedMetadata ->
                parsedMetadata.interfaceDeclarations.map {
                    EffectPair(parsedMetadata.implementationClassName, it.toClassName())
                }
            }
            .toSet()
    }

    /**
     * Each iteration of the loop receives a KSClassDeclaration of an interface
     * plus all ParsedMetadata instances associated with this interface.
     */
    public fun forEach(block: (KSClassDeclarationWrapper, List<ParsedMetadata>) -> Unit) {
        groupedMetadata.forEach { entry ->
            val interfaceDeclaration = entry.value
                .first()
                .interfaceDeclarations
                .first { it.toClassName() == entry.key }
            block(interfaceDeclaration, entry.value)
        }
    }

}

public data class EffectPair(
    val implementationClass: ClassName,
    val interfaceClass: ClassName,
)

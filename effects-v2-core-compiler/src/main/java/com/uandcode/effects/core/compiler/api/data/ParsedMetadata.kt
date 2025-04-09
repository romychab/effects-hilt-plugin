package com.uandcode.effects.core.compiler.api.data

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.core.compiler.api.extensions.KSClassDeclarationWrapper

/**
 * Open class that represents a parsed metadata annotation.
 * Subclasses can be created in plugins that extend this core compiler.
 */
public open class ParsedMetadata(
    public val interfaceDeclarations: List<KSClassDeclarationWrapper>,
    public val implementationClassDeclaration: KSClassDeclarationWrapper,
    public val metadataDeclaration: KSClassDeclaration? = null,
) : HasDependencies {

    public val implementationClassName: ClassName = implementationClassDeclaration.toClassName()
    override val dependencies: Dependencies by lazy { buildDependencies() }

    public constructor(
        effect: ParsedEffect,
    ) : this(
        interfaceDeclarations = effect.targetInterfaces,
        implementationClassDeclaration = effect.classDeclaration,
    )

    private fun buildDependencies(): Dependencies {
        return if (metadataDeclaration == null) {
            Dependencies(
                aggregating = true,
                checkNotNull(implementationClassDeclaration.containingFile),
            )
        } else {
            Dependencies.ALL_FILES
        }
    }

}

public fun List<Dependencies>.aggregate(): Dependencies {
    val isAllFiles = any { it == Dependencies.ALL_FILES }
    if (isAllFiles) return Dependencies.ALL_FILES

    val isAggregating = any { it.aggregating }
    val gatheredFiles = map { it.originatingFiles }.flatten()

    return Dependencies(
        isAggregating, *gatheredFiles.toTypedArray()
    )
}

public fun List<HasDependencies>.aggregateDependencies(): Dependencies =
    map { it.dependencies }.aggregate()
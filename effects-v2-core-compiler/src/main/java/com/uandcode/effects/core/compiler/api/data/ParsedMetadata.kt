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
    public val applicationClassDeclaration: KSClassDeclaration,
    public val interfaceDeclaration: KSClassDeclarationWrapper,
    public val implementationClassDeclaration: KSClassDeclarationWrapper,
    public val metadataDeclaration: KSClassDeclaration? = null,
) {

    public val implementationClassName: ClassName = implementationClassDeclaration.toClassName()
    public val interfaceClassName: ClassName by lazy { interfaceDeclaration.toClassName() }
    public val interfaceName: String get() = interfaceClassName.simpleName
    public val pkg: String = interfaceDeclaration.packageName.asString()
    public val dependencies: Dependencies by lazy { buildDependencies() }

    public constructor(
        effect: ParsedEffect,
        applicationClass: KSClassDeclaration,
    ) : this(
        applicationClassDeclaration = applicationClass,
        interfaceDeclaration = effect.targetInterface,
        implementationClassDeclaration = effect.classDeclaration,
    )

    private fun buildDependencies(): Dependencies {
        val files = listOfNotNull(
            interfaceDeclaration.containingFile,
            implementationClassDeclaration.containingFile,
            metadataDeclaration?.containingFile,
            applicationClassDeclaration.containingFile,
        ).toTypedArray()
        return Dependencies(
            aggregating = false,
            *files,
        )
    }

}
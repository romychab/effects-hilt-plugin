package com.uandcode.effects.compiler.common.api

import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.TypeSpec
import com.uandcode.effects.compiler.common.api.data.TemplateBasedClassContent

/**
 * Interface for commiting prepared auto-generated classes.
 */
public interface KspClassWriter {

    /**
     * Save the class described by the specified [TypeSpec].
     *
     * @param dependencies input dependencies for the class which were used upon the generation process.
     * @param pkg destination package name where the class wil be written to.
     */
    public fun write(
        typeSpec: TypeSpec,
        dependencies: Dependencies,
        pkg: String,
    )

    /**
     * Save the class described by the specified [TemplateBasedClassContent].
     *
     * @see TemplateBasedClassContent
     */
    public fun write(templateBasedClassContent: TemplateBasedClassContent)

}

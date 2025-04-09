package com.uandcode.effects.core.compiler.api

import com.uandcode.effects.core.compiler.Const
import com.uandcode.effects.core.compiler.api.data.ParsedEffect
import com.uandcode.effects.core.compiler.api.data.TemplateBasedClassContent

/**
 * Base class for generating metadata annotations. Other plugins can extend this class.
 * At least, they should use their own templates for generating metadata.
 *
 * The template should be a Kotlin file with the following content:
 *
 * ```
 * %PACKAGE_STATEMENT%
 *
 * // imports if needed
 *
 * @YourCustomMetadataAnnotation(
 *     // required parameters:
 *     interfaceClassName = "%EFFECT_INTERFACE_CLASSNAME%",
 *     implementationClassName = "%EFFECT_IMPL_CLASSNAME%",
 *     cleanUpMethodName = "%CLEAN_UP_METHOD_NAME%",
 *     // other custom parameters if needed
 * )
 * public class %CLASSNAME%
 * ```
 */
public abstract class AbstractMetadataGenerator(
    private val templatePath: String,
    private val writer: KspClassWriter,
) {

    public fun generate(effect: ParsedEffect) {
        val prefixName = effect.className.canonicalName.replace('.', '_')
        val classContent = TemplateBasedClassContent(
            templatePath = templatePath,
            pkg = Const.MetadataPackage,
            className = "__${prefixName}_Metadata",
            dependencies = effect.dependencies,
        ).apply {
            setVariable("EFFECT_IMPL_CLASSNAME", effect.className.canonicalName)
            setVariable("EFFECT_INTERFACE_CLASSNAMES", effect.targetInterfaceNames())
            setupVariables(effect)
        }
        writer.write(classContent)
    }

    public open fun TemplateBasedClassContent.setupVariables(parsedEffect: ParsedEffect): Unit = Unit

    private fun ParsedEffect.targetInterfaceNames(): String {
        return targetInterfaces
            .mapNotNull { it.qualifiedName?.asString() }
            .joinToString(", ") { "\"${it}\"" }
            .let { "[$it]" }
    }

}

package com.uandcode.effects.core.compiler.api.data

import com.google.devtools.ksp.processing.Dependencies
import com.uandcode.effects.core.compiler.exceptions.InternalEffectKspException

/**
 * Generate a class based on a template from the resources.
 *
 * The template must contain the following placeholders:
 * - '%PACKAGE_STATEMENT%' - will be replaced with the [pkg] value.
 * - '%CLASSNAME%' - will be replaced with the [className] value.
 *
 * Other placeholders can be defined by the user using [setVariable] method.
 *
 * Example if valid template:
 *
 * ```
 * %PACKAGE_STATEMENT% // <-- required placeholder
 *
 * import com.uandcode.effects.core.annotations.EffectMetadata
 *
 * @EffectMetadata(
 *     // custom placeholder:
 *     interfaceClassName = "%EFFECT_IMPL_CLASSNAME%",
 * )
 * public class %CLASSNAME% // <-- required placeholder
 * ```
 *
 * @param templatePath path to the template in the resources.
 * @param pkg package name where the class will be written to.
 * @param className name of the class to be generated.
 * @param dependencies input dependencies for the class which were used upon the generation process.
 */
public class TemplateBasedClassContent(
    internal val templatePath: String,
    internal val pkg: String,
    internal val className: String,
    internal val dependencies: Dependencies,
) {

    private val variables = mutableMapOf<String, String>()

    public fun setVariable(name: String, value: String): TemplateBasedClassContent =
        apply { variables[name] = value }

    public fun buildContent(): String {
        val sourceContent = javaClass.getResourceAsStream("/$templatePath")?.reader().use {
            it?.readText()
        } ?: throw InternalEffectKspException("Can't open template resource: $templatePath")
        var resultContent = sourceContent
        variables.forEach { (key, value) ->
            resultContent = resultContent.replace("%${key}%", value)
        }
        val packageStatement = if (pkg.isBlank()) {
            ""
        } else {
            "package $pkg"
        }
        val className = this.className
        resultContent = resultContent.replace("%PACKAGE_STATEMENT%", packageStatement)
        resultContent = resultContent.replace("%CLASSNAME%", className)
        return resultContent
    }

}

package com.uandcode.effects.compiler.common.api.data

import com.google.devtools.ksp.processing.Dependencies
import com.uandcode.effects.compiler.common.exceptions.InternalEffectKspException

/**
 * Generate a class based on a template from the resources.
 *
 * The template must contain the following placeholders:
 * - '%PACKAGE_STATEMENT%' - will be replaced with the [pkg] value.
 * - '%CLASSNAME%' - will be replaced with the [className] value.
 * - '%EFFECT_INTERFACE_CLASSNAME%' - will be replaced with the target effect interface
 * - '%EFFECT_IMPL_CLASSNAME%' - will be replaced with a classname of the annotated class
 *
 * Other placeholders can be defined by the user using [setVariable] method.
 *
 * Example of valid template:
 *
 * ```
 * %PACKAGE_STATEMENT% // <-- required placeholder
 *
 * import com.uandcode.effects.core.annotations.EffectMetadata
 *
 * @EffectMetadata(
 *     // required params and placeholders:
 *     interfaceClassName = "%EFFECT_INTERFACE_CLASSNAME%",
 *     implementationClassName = "%EFFECT_IMPL_CLASSNAME%",
 *     // custom param and placeholder:
 *     yourAdditionalParam = "%PARAM_NAME%",
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

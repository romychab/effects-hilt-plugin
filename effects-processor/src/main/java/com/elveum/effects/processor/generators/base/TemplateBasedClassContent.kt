package com.elveum.effects.processor.generators.base

import com.elveum.effects.processor.exceptions.InternalEffectKspException
import com.google.devtools.ksp.processing.Dependencies

class TemplateBasedClassContent(
    private val templatePath: String,
    val pkg: String,
    val className: String,
    val dependencies: Dependencies,
) {

    private val variables = mutableMapOf<String, String>()

    fun setVariable(name: String, value: String) = apply { variables[name] = value }

    fun buildContent(): String {
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

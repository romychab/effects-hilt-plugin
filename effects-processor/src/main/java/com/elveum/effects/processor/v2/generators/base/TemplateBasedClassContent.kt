package com.elveum.effects.processor.v2.generators.base

import com.elveum.effects.processor.v2.InternalEffectKspException
import com.google.devtools.ksp.processing.Dependencies

class TemplateBasedClassContent private constructor(
    val pkg: String,
    val className: String,
    val dependencies: Dependencies,
    val content: String,
) {

    class Builder(
        private val templatePath: String,
    ) {
        private var dependencies: Dependencies? = null
        private var className: String? = null
        private var pkg: String? = null
        private val variables = mutableMapOf<String, String>()

        fun setDependencies(dependencies: Dependencies): Builder {
            this.dependencies = dependencies
            return this
        }

        fun setClassName(name: String): Builder {
            this.className = name
            return this
        }

        fun setPackage(pkg: String): Builder {
            this.pkg = pkg
            return this
        }

        fun setVariable(name: String, value: String): Builder {
            variables[name] = value
            return this
        }

        /**
         * @throws InternalEffectKspException if not all builder params are specified
         */
        fun build(): TemplateBasedClassContent {
            val sourceContent = javaClass.getResourceAsStream("/$templatePath")?.reader().use {
                it?.readText()
            } ?: throw InternalEffectKspException("Can't open template resource: $templatePath")
            var resultContent = sourceContent
            variables.forEach { (key, value) ->
                resultContent = resultContent.replace("%${key}%", value)
            }
            return TemplateBasedClassContent(
                pkg = this.pkg ?: throw InternalEffectKspException("setPackage hasn't been called"),
                className = this.className ?: throw InternalEffectKspException("setClassName hasn't been called"),
                dependencies = this.dependencies ?: throw InternalEffectKspException("setDependencies hasn't been called"),
                content = resultContent,
            )
        }
    }

}

package com.uandcode.effects.hilt.compiler.generators

import com.google.devtools.ksp.processing.Dependencies
import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.TemplateBasedClassContent
import com.uandcode.effects.hilt.compiler.Const

/**
 * Emits the Hilt modules and entry points that used to be shipped inside the
 * library AAR. They are written into the module that aggregates metadata,
 * which is the application module, so they are compiled by the Hilt version
 * the consuming project uses.
 */
class HiltCoreModuleGenerator(
    private val writer: KspClassWriter,
) {

    fun generate() {
        CORE_CLASSES.forEach { (className, templateFileName) ->
            writer.write(
                TemplateBasedClassContent(
                    className = className,
                    pkg = Const.GeneratedPackage,
                    templatePath = "${Const.CoreTemplateDir}/$templateFileName",
                    dependencies = Dependencies(aggregating = true),
                )
            )
        }
    }

    private companion object {
        val CORE_CLASSES = listOf(
            "SingletonEffectModule" to "SingletonEffectModuleTemplate.kt",
            "ActivityRetainedEffectModule" to "ActivityRetainedEffectModuleTemplate.kt",
            "ViewModelEffectModule" to "ViewModelEffectModuleTemplate.kt",
            "ActivityEffectModule" to "ActivityEffectModuleTemplate.kt",
            "FragmentEffectModule" to "FragmentEffectModuleTemplate.kt",
            "MultibindingModule" to "MultibindingModuleTemplate.kt",
            "GeneratedActivityEffectEntryPoint" to "ActivityEntryPointTemplate.kt",
            "GeneratedFragmentEffectEntryPoint" to "FragmentEntryPointTemplate.kt",
        )
    }
}

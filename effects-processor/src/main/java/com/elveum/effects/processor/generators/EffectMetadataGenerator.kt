package com.elveum.effects.processor.generators

import com.elveum.effects.processor.data.Const
import com.elveum.effects.processor.data.EffectInfo
import com.elveum.effects.processor.generators.base.KspClassWriter
import com.elveum.effects.processor.generators.base.TemplateBasedClassContent
import com.google.devtools.ksp.processing.Dependencies

class EffectMetadataGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(effectInfo: EffectInfo) {
        val prefixName = effectInfo.effectClassName.canonicalName.replace('.', '_')
        val dependencies = Dependencies(
            aggregating = false,
            checkNotNull(effectInfo.effectClassDeclaration.containingFile)
        )
        val classContent = TemplateBasedClassContent(
            templatePath = "EffectMetadataTemplate.kt",
            pkg = Const.MetadataPackage,
            className = "__${prefixName}_Metadata",
            dependencies = dependencies,
        ).apply {
            setVariable("EFFECT_IMPL_CLASSNAME", effectInfo.effectClassName.canonicalName)
            setVariable("TARGET_INTERFACE_CLASSNAMES", effectInfo.targetInterfaceNames())
            setVariable("HILT_COMPONENT_CLASSNAME", effectInfo.hiltComponent.canonicalName)
            setVariable("HILT_SCOPE_CLASSNAME", effectInfo.hiltScope.canonicalName)
            setVariable("CLEAN_UP_METHOD_NAME", effectInfo.cleanUpMethodName.originCleanUpMethodName)
        }
        writer.write(classContent)
    }

    private fun EffectInfo.targetInterfaceNames(): String {
        return targetInterfaceList
            .mapNotNull { it.qualifiedName?.asString() }
            .joinToString(", ") { "\"${it}\"" }
            .let { "[$it]" }
    }

}

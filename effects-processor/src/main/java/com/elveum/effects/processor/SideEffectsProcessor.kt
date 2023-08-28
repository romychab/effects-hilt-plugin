package com.elveum.effects.processor

import com.elveum.effects.processor.generators.ImplementationModuleGenerator
import com.elveum.effects.processor.generators.KotlinMediatorGenerator
import com.elveum.effects.processor.generators.MediatorModuleGenerator
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@KotlinPoetJavaPoetPreview
@SupportedAnnotationTypes(value = [
    "com.elveum.effects.annotations.SideEffect"
])
class SideEffectsProcessor : BaseProcessor() {

    private val mediatorGenerator: KotlinMediatorGenerator by lazy {
        KotlinMediatorGenerator(types, elements, filer)
    }

    private val mediatorModuleGenerator: MediatorModuleGenerator by lazy {
        MediatorModuleGenerator(filer)
    }

    private val implementationModuleGenerator: ImplementationModuleGenerator by lazy {
        ImplementationModuleGenerator(elements, filer)
    }

    override fun generateFor(element: TypeElement) {
        val result = mediatorGenerator.generateMediatorClass(element)
        val key = mediatorModuleGenerator.generateModule(result.parsedElements, result.typeSpec)
        implementationModuleGenerator.generateModule(result.parsedElements, key)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_8
    }

}
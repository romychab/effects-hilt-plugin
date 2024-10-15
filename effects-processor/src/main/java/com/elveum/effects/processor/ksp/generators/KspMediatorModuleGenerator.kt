package com.elveum.effects.processor.ksp.generators

import com.elveum.effects.processor.ksp.KspNames
import com.elveum.effects.processor.ksp.KspParsedElements
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName

class KspMediatorModuleGenerator {

    fun generateModule(result: KspMediatorGenerator.Result): String {
        val parsedElements = result.parsedElements
        val generatedMediatorSpec = result.generatedMediatorTypeSpec
        val plainKey = parsedElements.directInterface.qualifiedName!!.asString()
        val key = if (parsedElements.qualifier == null)
            plainKey
        else
            "${parsedElements.qualifier.pkg}::${parsedElements.qualifier.name}::$plainKey"

        val moduleName = "${generatedMediatorSpec.name}Module"
        val builder = TypeSpec.objectBuilder(moduleName)
            .addModifiers(KModifier.PUBLIC)
            .addAnnotation(KspNames.moduleAnnotation)
            .addAnnotation(AnnotationSpec.builder(KspNames.installInAnnotation)
                .addMember("%T::class", KspNames.activityRetainedComponentName)
                .build())
            .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class)
                .addMember("\"unchecked\"")
                .build())
            .addAnnotation(AnnotationSpec.builder(Suppress::class)
                .addMember("\"UNCHECKED_CAST\"")
                .build())
            .generateProvider(generatedMediatorSpec, parsedElements)
            .generateSidePair(generatedMediatorSpec, parsedElements, key)

        result.writer.write(
            name = moduleName,
            typeSpec = builder.build()
        )

        return key
    }

    private fun TypeSpec.Builder.generateProvider(
        generatedMediatorSpec: TypeSpec,
        parsedElements: KspParsedElements,
    ): TypeSpec.Builder {
        val methodBuilder = FunSpec.builder("provide${generatedMediatorSpec.name}")
            .addParameter(ParameterSpec.builder("scope", KspNames.coroutineScope)
                .addAnnotation(KspNames.sideEffectsScope)
                .build()
            )
            .addAnnotation(KspNames.providesAnnotation)
            .addAnnotation(KspNames.activityRetainedScope)
            .addCode("return %T(scope);", ClassName(parsedElements.pkg, generatedMediatorSpec.name!!))
            .returns(parsedElements.directInterface.toClassName())
        if (parsedElements.qualifier != null) {
            methodBuilder.addAnnotation(ClassName(parsedElements.qualifier.pkg, parsedElements.qualifier.name))
        }
        addFunction(methodBuilder.build())
        return this
    }


    private fun TypeSpec.Builder.generateSidePair(
        kotlinTypeSpec: TypeSpec,
        parsedElements: KspParsedElements,
        key: String
    ): TypeSpec.Builder {
        val methodBuilder = FunSpec.builder("provide${kotlinTypeSpec.name}SidePair")
            .addAnnotation(KspNames.providesAnnotation)
            .addAnnotation(KspNames.activityRetainedScope)
            .addAnnotation(KspNames.intoSet)
            .returns(KspNames.sidePair)
            .addCode(
                "return %T(\"$key\", mediator as %T);",
                KspNames.sidePair, KspNames.sideEffectMediator(ANY)
            )
        val parameterType = parsedElements.directInterface.toClassName()
        val parameterName = "mediator"
        if (parsedElements.qualifier != null) {
            methodBuilder.addParameter(ParameterSpec.builder(parameterName, parameterType)
                .addAnnotation(ClassName(parsedElements.qualifier.pkg, parsedElements.qualifier.name))
                .build())
        } else {
            methodBuilder.addParameter(parameterName, parameterType)
        }
        addFunction(methodBuilder.build())
        return this
    }
}

package com.elveum.effects.processor.ksp.generators

import com.elveum.effects.processor.ksp.KspNames
import com.elveum.effects.processor.ksp.KspParsedElements
import com.elveum.effects.processor.ksp.isQualifier
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class KspImplementationModuleGenerator {

    fun generateModule(
        writer: KspClassWriter,
        parsedElements: KspParsedElements,
        key: String
    ) {
        val moduleName = "${parsedElements.originName}Module"
        val builder = TypeSpec.objectBuilder(moduleName)
            .addModifiers(KModifier.PUBLIC)
            .addAnnotation(KspNames.moduleAnnotation)
            .addAnnotation(AnnotationSpec.builder(KspNames.installInAnnotation)
                    .addMember("%T::class", KspNames.activityComponentName)
                    .build())
            .generateImplementationProvider(parsedElements)
            .generateMviEffectImplementation(key, parsedElements)
            .generateOtherInterfacesProviders(parsedElements)

        writer.write(
            moduleName,
            builder.build()
        )
    }

    private fun TypeSpec.Builder.generateImplementationProvider(
        parsedElements: KspParsedElements
    ): TypeSpec.Builder {
        val constructorParams = parsedElements.origin.getConstructors()
            .maxByOrNull { it.parameters.size }
            ?.parameters ?: emptyList()

        val params = constructorParams
            .filter { !it.hasDefault }
            .map {
                val parameterSpecBuilder = ParameterSpec.builder(
                    it.name!!.getShortName(),
                    it.type.toTypeName()
                )
                it.annotations.forEach { annotation ->
                    if (annotation.isQualifier()) {
                        val annotationClass = annotation.annotationType.toTypeName() as ClassName
                        parameterSpecBuilder.addAnnotation(annotationClass)
                    }
                }
                parameterSpecBuilder.build()
            }
        var hasRetainedData = false
        val paramsString = params.joinToString(", ") {
            val className = it.type as? ClassName
            if (className?.reflectionName() == KspNames.retainedData.reflectionName()) {
                hasRetainedData = true
                "${it.name} = %T(${it.name}, \"${parsedElements.pkg}.${parsedElements.originName}\")"
            } else {
                "${it.name} = ${it.name}"
            }
        }
        val name = ClassName(parsedElements.pkg, parsedElements.originName)
        val methodBuilder = FunSpec.builder("provide${parsedElements.originName}InstanceImpl")
            .addParameters(params)
            .addModifiers(KModifier.PUBLIC)
            .addAnnotation(KspNames.providesAnnotation)
            .addAnnotation(KspNames.activityScope)
            .returns(ClassName(parsedElements.pkg, parsedElements.originName))
        val code = "return %T($paramsString)"
        if (hasRetainedData) {
            methodBuilder.addCode(code, name, KspNames.wrappedRetainedData)
        } else {
            methodBuilder.addCode(code, name)
        }
        if (parsedElements.qualifier != null) {
            methodBuilder.addAnnotation(ClassName(parsedElements.qualifier.pkg, parsedElements.qualifier.name))
        }
        addFunction(methodBuilder.build())
        return this
    }

    private fun TypeSpec.Builder.generateMviEffectImplementation(
        key: String,
        parsedElements: KspParsedElements,
    ): TypeSpec.Builder {
        val methodBuilder = FunSpec.builder("provide${parsedElements.originName}MviEffectImpl")
            .addModifiers(KModifier.PUBLIC)
            .addAnnotation(KspNames.providesAnnotation)
            .addAnnotation(KspNames.activityScope)
            .addAnnotation(KspNames.intoSet)
            .returns(KspNames.mviImplementation)
            .addCode(
                "return %T(instance, \"$key\");",
                KspNames.mviImplementation
            )
        methodBuilder.addParameter(createParameterSpec(parsedElements))
        addFunction(methodBuilder.build())
        return this
    }

    private fun createParameterSpec(
        parsedElements: KspParsedElements
    ): ParameterSpec {
        val parameterTypeName = ClassName(parsedElements.pkg, parsedElements.originName)
        val parameterName = "instance"
        return if (parsedElements.qualifier == null) {
            ParameterSpec.builder(parameterName, parameterTypeName).build()
        } else {
            ParameterSpec.builder(parameterName, parameterTypeName)
                .addAnnotation(ClassName(parsedElements.qualifier.pkg, parsedElements.qualifier.name))
                .build()
        }
    }

    private fun TypeSpec.Builder.generateOtherInterfacesProviders(
        parsedElements: KspParsedElements
    ): TypeSpec.Builder {
        parsedElements.otherInterfaces.forEachIndexed { index, typeElement ->
            generateOtherInterfaceProvider(index + 1, typeElement, parsedElements)
        }
        return this
    }

    private fun TypeSpec.Builder.generateOtherInterfaceProvider(
        index: Int,
        otherInterface: KSClassDeclaration,
        parsedElements: KspParsedElements,
    ) {
        val methodBuilder = FunSpec.builder("provide${otherInterface.simpleName.getShortName()}Impl_$index")
            .addModifiers(KModifier.PUBLIC)
            .addAnnotation(KspNames.providesAnnotation)
            .returns(otherInterface.toClassName())
            .addCode("return instance")
        methodBuilder.addParameter(createParameterSpec(parsedElements))
        addFunction(methodBuilder.build())
    }
}

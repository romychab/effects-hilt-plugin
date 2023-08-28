package com.elveum.effects.processor.generators

import com.elveum.effects.processor.Names
import com.elveum.effects.processor.ParsedElements
import com.squareup.javapoet.*
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements

/**
 * This generator creates a Dagger/Hilt module which provides your implementation
 * (class annotated by `SideEffect`) to the Hilt `ActivityComponent` making it
 * accessible from activities and fragments.
 */
@KotlinPoetJavaPoetPreview
class ImplementationModuleGenerator(
    private val elements: Elements,
    private val filer: Filer,
) {
    fun generateModule(
        parsedElements: ParsedElements,
        key: String
    ) {
        val moduleName = "${parsedElements.originName}Module"
        val builder = TypeSpec.classBuilder(moduleName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Names.moduleAnnotation)
            .addAnnotation(
                AnnotationSpec.builder(Names.installInAnnotation)
                .addMember("value", "{\$T.class}", Names.activityComponentName)
                .build())
            .generateImplementationProvider(parsedElements)
            .generateSideImplementation(key, parsedElements)
            .generateOtherInterfacesProviders(parsedElements)

        val result = builder.build()
        JavaFile.builder(parsedElements.pkg, result)
            .build()
            .writeTo(filer)
    }

    private fun TypeSpec.Builder.generateOtherInterfacesProviders(
        parsedElements: ParsedElements
    ): TypeSpec.Builder {
        parsedElements.otherInterfaces.forEachIndexed { index, typeElement ->
            generateOtherInterfaceProvider(index + 1, typeElement, parsedElements)
        }
        return this
    }

    private fun TypeSpec.Builder.generateOtherInterfaceProvider(
        index: Int,
        otherInterface: TypeElement,
        parsedElements: ParsedElements
    ) {
        val methodBuilder = MethodSpec.methodBuilder("provide${otherInterface.simpleName.toString()}Impl_$index")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Names.providesAnnotation)
            .returns(ClassName.get(otherInterface))
            .addCode("return instance;")
        methodBuilder.addParameter(createParameterSpec(parsedElements))
        addMethod(methodBuilder.build())
    }

    private fun TypeSpec.Builder.generateImplementationProvider(
        parsedElements: ParsedElements
    ): TypeSpec.Builder {
        val constructorParams = ElementFilter.constructorsIn(parsedElements.origin.enclosedElements)
            .maxByOrNull { it.parameters.size }
            ?.parameters ?: emptyList()

        val params = constructorParams
            .map {
                val parameterSpecBuilder = ParameterSpec.builder(
                    TypeName.get(it.asType()),
                    it.simpleName.toString()
                )
                it.annotationMirrors.forEach { annotationMirror ->
                    val annotationElement = annotationMirror.annotationType.asElement()
                    val annotationSpec = AnnotationSpec.builder(
                        ClassName.get(elements.getPackageOf(annotationElement).toString(), annotationElement.simpleName.toString())
                    )
                    .build()
                    parameterSpecBuilder.addAnnotation(annotationSpec)
                }
                parameterSpecBuilder.build()
            }
        var hasRetainedData = false
        val paramsString = params.joinToString(", ") {
            if (it.type.toString() == Names.retainedData.canonicalName()) {
                hasRetainedData = true
                "new \$T(${it.name}, \"${parsedElements.pkg}.${parsedElements.originName}\")"
            } else {
                it.name
            }
        }
        val name = ClassName.get(parsedElements.pkg, parsedElements.originName)
        val methodBuilder = MethodSpec.methodBuilder("provide${parsedElements.originName}InstanceImpl")
            .addParameters(params)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Names.providesAnnotation)
            .addAnnotation(Names.activityScope)
            .returns(ClassName.get(parsedElements.pkg, parsedElements.originName))
        val code = "return new \$T($paramsString);"
        if (hasRetainedData) {
            methodBuilder.addCode(code, name, Names.wrappedRetainedData)
        } else {
            methodBuilder.addCode(code, name)
        }
        if (parsedElements.qualifier != null) {
            methodBuilder.addAnnotation(parsedElements.qualifier.toJClassName())
        }
        addMethod(methodBuilder.build())

        return this

    }

    private fun TypeSpec.Builder.generateSideImplementation(
        key: String,
        parsedElements: ParsedElements
    ): TypeSpec.Builder {
        val methodBuilder = MethodSpec.methodBuilder("provide${parsedElements.originName}SideEffectImpl")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Names.providesAnnotation)
            .addAnnotation(Names.activityScope)
            .addAnnotation(Names.intoSet)
            .returns(Names.sideImplementation)
            .addCode(
                "return new \$T(instance, \"$key\");",
                Names.sideImplementation
            )
        methodBuilder.addParameter(createParameterSpec(parsedElements))
        addMethod(methodBuilder.build())

        return this
    }

    private fun createParameterSpec(
        parsedElements: ParsedElements
    ): ParameterSpec {
        val parameterTypeName = ClassName.get(parsedElements.pkg, parsedElements.originName)
        val parameterName = "instance"
        if (parsedElements.qualifier == null) {
            return ParameterSpec.builder(parameterTypeName, parameterName)
                .build()
        } else {
            return ParameterSpec.builder(parameterTypeName, parameterName)
                .addAnnotation(parsedElements.qualifier.toJClassName())
                .build()
        }
    }

}
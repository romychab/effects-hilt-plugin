package com.elveum.effects.processor.generators

import com.elveum.effects.processor.Names
import com.elveum.effects.processor.ParsedElements
import com.squareup.javapoet.*
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * This generator creates a Hilt/Dagger module which provides an implementation
 * of the MVI-effect interface to the Hilt `ActivityScopedComponent` making it
 * accessible from view-models, activities and fragments.
 *
 * Output module name: `GeneratedMediator{ImplementationClassName}Module`.
 */
@KotlinPoetJavaPoetPreview
class MediatorModuleGenerator(
    private val filer: Filer,
) {

    fun generateModule(parsedElements: ParsedElements, kotlinTypeSpec: com.squareup.kotlinpoet.TypeSpec): String {
        val plainKey = parsedElements.directInterface.qualifiedName.toString()
        val key = if (parsedElements.qualifier == null)
            plainKey
        else
            "${parsedElements.qualifier.pkg}::${parsedElements.qualifier.name}::$plainKey"

        val moduleName = "${kotlinTypeSpec.name}Module"
        val builder = TypeSpec.classBuilder(moduleName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Names.moduleAnnotation)
            .addAnnotation(AnnotationSpec.builder(Names.installInAnnotation)
                .addMember("value", "{\$T.class}", Names.activityRetainedComponentName)
                .build())
            .addAnnotation(AnnotationSpec.builder(SuppressWarnings::class.java)
                .addMember("value", "\"unchecked\"")
                .build())
            .generateProvider(kotlinTypeSpec, parsedElements)
            .generateMviPair(kotlinTypeSpec, parsedElements, key)

        val result = builder.build()
        JavaFile.builder(parsedElements.pkg, result)
            .build()
            .writeTo(filer)
        return key
    }

    private fun TypeSpec.Builder.generateProvider(
        kotlinTypeSpec: com.squareup.kotlinpoet.TypeSpec,
        parsedElements: ParsedElements
    ): TypeSpec.Builder {
        val methodBuilder = MethodSpec.methodBuilder("provide${kotlinTypeSpec.name}")
            .addParameter(ParameterSpec.builder(Names.coroutineScope, "scope")
                .addAnnotation(Names.mviEffectsScope)
                .build()
            )
            .addAnnotation(Names.providesAnnotation)
            .addAnnotation(Names.activityRetainedScope)
            .addCode("return new \$T(scope);", ClassName.get(parsedElements.pkg, kotlinTypeSpec.name))
            .returns(ClassName.get(parsedElements.directInterface))
        if (parsedElements.qualifier != null) {
            methodBuilder.addAnnotation(parsedElements.qualifier.toJClassName())
        }
        addMethod(methodBuilder.build())
        return this
    }

    private fun TypeSpec.Builder.generateMviPair(
        kotlinTypeSpec: com.squareup.kotlinpoet.TypeSpec,
        parsedElements: ParsedElements,
        key: String
    ): TypeSpec.Builder {
        val methodBuilder = MethodSpec.methodBuilder("provide${kotlinTypeSpec.name}MviPair")
            .addAnnotation(Names.providesAnnotation)
            .addAnnotation(Names.activityRetainedScope)
            .addAnnotation(Names.intoSet)
            .returns(Names.mviPair)
            .addCode(
                "return new \$T(\"$key\", (\$T) mediator);",
                Names.mviPair, Names.mviEffectMediator(ClassName.OBJECT)
            )
        val parameterType = ClassName.get(parsedElements.directInterface)
        val parameterName = "mediator"
        if (parsedElements.qualifier != null) {
            methodBuilder.addParameter(ParameterSpec.builder(parameterType, parameterName)
                .addAnnotation(parsedElements.qualifier.toJClassName())
                .build())
        } else {
            methodBuilder.addParameter(parameterType, parameterName)
        }
        addMethod(methodBuilder.build())
        return this
    }

}
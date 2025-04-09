package com.elveum.effects.processor.generators

import com.elveum.effects.processor.data.Const
import com.elveum.effects.processor.data.EffectInfo
import com.elveum.effects.processor.generators.base.KspClassWriter
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName

class EffectImplementationHiltModuleGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(effectInfo: EffectInfo) {
        val name = "${effectInfo.effectName}ImplModule"
        val pkg = effectInfo.pkg
        val typeSpec = TypeSpec.objectBuilder(ClassName(pkg, name))
            .addModifiers(KModifier.INTERNAL)
            .addAnnotation(Const.ModuleAnnotationName)
            .addAnnotation(AnnotationSpec.builder(Const.InstallInAnnotationName)
                .addMember("%T::class", effectInfo.hiltComponent)
                .build())
            .addProvideControllerMethod(effectInfo.effectClassName, effectInfo.targetInterfaceList)
            .addProvideEffectRecordMethod(effectInfo.effectClassName, effectInfo.targetInterfaceList)
            .build()

        val dependencies = Dependencies(
            aggregating = false,
            checkNotNull(effectInfo.effectClassDeclaration.containingFile),
        )
        writer.write(typeSpec, dependencies, pkg)
    }

    private fun TypeSpec.Builder.addProvideControllerMethod(
        effectClassName: ClassName,
        interfaces: List<KSClassDeclaration>,
    ) = apply {
        addFunction(
            FunSpec.builder("provideControllerOfEffectImpl")
                .addAnnotation(Const.ProvidesAnnotationName)
                .addAnnotation(AnnotationSpec.builder(Const.SuppressAnnotationName)
                    .addMember("\"UNCHECKED_CAST\"")
                    .build())
                .apply {
                    val params = interfaces.mapIndexed { index, interfaceDeclaration ->
                        val interfaceClassName = interfaceDeclaration.toClassName()
                        val paramType = Const.observableResourceStoreName(interfaceClassName)
                        ParameterSpec.builder("store$index", paramType).build()
                    }
                    addParameters(params)
                    val paramNames = params.joinToString(", ") { it.name }
                    addCode("val stores = listOf($paramNames) as List<%T>\n", Const.observableResourceStoreName(effectClassName))
                    addCode("return %T(stores)", Const.EffectControllerImplName)
                }
                .returns(Const.effectControllerName(effectClassName))
                .build()
        )
    }

    private fun TypeSpec.Builder.addProvideEffectRecordMethod(
        effectClassName: ClassName,
        interfaces: List<KSClassDeclaration>,
    ) = apply {
        val controllerType = Const.effectControllerName(effectClassName)
        val providerType = Const.providerName(controllerType)
        val interfaceClassNames = interfaces.map { it.toClassName() }
        val interfaceClassesStr = interfaceClassNames.joinToString(", ") { "%T::class" }
        addFunction(
            FunSpec.builder("provideEffectRecord")
                .addAnnotation(Const.ProvidesAnnotationName)
                .addAnnotation(Const.IntoSetAnnotationName)
                .addParameter("provider", providerType)
                .addCode(
                    format = """
                       return %T(
                           effectImplementationClass = %T::class, 
                           effectInterfaceClasses = listOf($interfaceClassesStr), 
                           controllerProvider = { provider.get() }
                       )                        
                    """.trimIndent(),
                    Const.EffectRecordName,
                    effectClassName,
                    *interfaceClassNames.toTypedArray(),
                )
                .returns(Const.EffectRecordName)
                .build()
        )
    }

}

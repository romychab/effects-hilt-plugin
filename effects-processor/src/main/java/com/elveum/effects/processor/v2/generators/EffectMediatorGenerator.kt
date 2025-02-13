package com.elveum.effects.processor.v2.generators

import com.elveum.effects.processor.v2.UnitCommandWithReturnTypeException
import com.elveum.effects.processor.v2.data.Const
import com.elveum.effects.processor.v2.data.EffectInfo
import com.elveum.effects.processor.v2.extensions.KSClassDeclarationWrapper
import com.elveum.effects.processor.v2.extensions.implementInterfaceMethod
import com.elveum.effects.processor.v2.extensions.primaryConstructorWithProperties
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver

class EffectMediatorGenerator(
    private val logger: KSPLogger,
    private val writer: KspClassV2Writer,
) {

    fun generate(effectInfo: EffectInfo): Result {
        val mediatorName = "${effectInfo.targetInterfaceName}Mediator"
        val mediatorClassName = ClassName(effectInfo.pkg, mediatorName)

        val typeSpecBuilder = TypeSpec.classBuilder(mediatorClassName)
            .addConstructor(effectInfo)
            .implementTargetInterface(effectInfo.targetInterface)

        writer.write(
            typeSpec = typeSpecBuilder.build(),
            pkg = mediatorClassName.packageName,
            dependencies = effectInfo.dependencies,
        )

        return Result(
            mediatorClassName = mediatorClassName,
        )
    }

    private fun TypeSpec.Builder.addConstructor(effectInfo: EffectInfo): TypeSpec.Builder {
        return primaryConstructorWithProperties(
            FunSpec.constructorBuilder()
                .addParameter(COMMAND_EXECUTOR_PROPERTY, Const.commandExecutorName(effectInfo.effectClassName))
                .build()
        )
    }

    private fun TypeSpec.Builder.implementTargetInterface(
        targetInterface: KSClassDeclarationWrapper,
    ): TypeSpec.Builder {
        return addSuperinterface(targetInterface.toClassName())
            .apply {
                targetInterface.getAllFunctions()
                    .filter { it.isAbstract }
                    .forEach { function ->
                        implementMethod(function)
                    }
            }
    }

    private fun TypeSpec.Builder.implementMethod(
        function: KSFunctionDeclaration,
    ) {
        val typeParameterResolver = function.typeParameters.toTypeParameterResolver()
        val funSpecBuilder = implementInterfaceMethod(function, typeParameterResolver)
        val params = funSpecBuilder.parameters.joinToString(", ") {
            if (it.modifiers.contains(KModifier.VARARG)) {
                "*${it.name}"
            } else {
                it.name
            }
        }
        val code = "it.${function.simpleName.asString()}($params)"
        when (getCommandType(typeParameterResolver, function)) {
            CommandType.UnitCommand -> {
                funSpecBuilder.addCode("$COMMAND_EXECUTOR_PROPERTY.execute { $code }")
            }
            CommandType.CoroutineCommand -> {
                funSpecBuilder.addCode("return $COMMAND_EXECUTOR_PROPERTY.executeCoroutine { $code }")
            }
            CommandType.FlowCommand -> {
                funSpecBuilder.addCode("return $COMMAND_EXECUTOR_PROPERTY.executeFlow { $code }")
            }
        }
        addFunction(funSpecBuilder.build())
    }

    private fun getCommandType(
        typeParameterResolver: TypeParameterResolver,
        function: KSFunctionDeclaration,
    ): CommandType {
        return when {
            function.modifiers.contains(Modifier.SUSPEND) -> CommandType.CoroutineCommand
            function.returnType?.isKotlinFlow(typeParameterResolver) == true -> CommandType.FlowCommand
            function.returnType?.isUnit(typeParameterResolver) == true -> CommandType.UnitCommand
            else -> throw UnitCommandWithReturnTypeException(function)
        }
    }

    private fun KSTypeReference.isKotlinFlow(
        typeParameterResolver: TypeParameterResolver,
    ): Boolean {
        return (toTypeName(typeParameterResolver) as? ParameterizedTypeName)
            ?.rawType == Const.FlowClassName
    }

    private fun KSTypeReference.isUnit(
        typeParameterResolver: TypeParameterResolver,
    ): Boolean {
        return toTypeName(typeParameterResolver) == UNIT
    }

    enum class CommandType {
        UnitCommand,
        CoroutineCommand,
        FlowCommand,
    }

    class Result(
        val mediatorClassName: ClassName,
    )

    companion object {
        val COMMAND_EXECUTOR_PROPERTY = "commandExecutor"
    }
}
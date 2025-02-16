package com.elveum.effects.processor.v2.generators

import com.elveum.effects.processor.v2.UnitCommandWithReturnTypeException
import com.elveum.effects.processor.v2.data.Const
import com.elveum.effects.processor.v2.data.EffectInfo
import com.elveum.effects.processor.v2.extensions.KSFunctionDeclarationWrapper
import com.elveum.effects.processor.v2.extensions.implementInterface
import com.elveum.effects.processor.v2.extensions.primaryConstructorWithProperties
import com.elveum.effects.processor.v2.generators.base.KspClassV2Writer
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName

class EffectMediatorGenerator(
    private val writer: KspClassV2Writer,
) {

    fun generate(effectInfo: EffectInfo): Result {
        val mediatorName = "${effectInfo.targetInterfaceName}Mediator"
        val mediatorClassName = ClassName(effectInfo.pkg, mediatorName)

        val typeSpecBuilder = TypeSpec.classBuilder(mediatorClassName)
            .addConstructor(effectInfo)
            .implementInterface(effectInfo.targetInterface, ::implementMethod)

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

    private fun implementMethod(
        function: KSFunctionDeclarationWrapper,
    ): CodeBlock {
        val params = function.paramSpecs.joinToString(", ") {
            if (it.modifiers.contains(KModifier.VARARG)) {
                "*${it.name}"
            } else {
                it.name
            }
        }
        val code = "it.${function.simpleNameString}($params)"
        return when (getCommandType(function)) {
            CommandType.UnitCommand -> {
                CodeBlock.of("$COMMAND_EXECUTOR_PROPERTY.execute { $code }")
            }
            CommandType.CoroutineCommand -> {
                CodeBlock.of("return $COMMAND_EXECUTOR_PROPERTY.executeCoroutine { $code }")
            }
            CommandType.FlowCommand -> {
                CodeBlock.of("return $COMMAND_EXECUTOR_PROPERTY.executeFlow { $code }")
            }
        }
    }

    private fun getCommandType(function: KSFunctionDeclarationWrapper): CommandType {
        return when {
            function.isCoroutineCommand() -> CommandType.CoroutineCommand
            function.isFlowCommand() -> CommandType.FlowCommand
            function.isUnitCommand() -> CommandType.UnitCommand
            else -> throw UnitCommandWithReturnTypeException(function)
        }
    }

    private fun KSFunctionDeclarationWrapper.isUnitCommand(): Boolean {
        return returnType?.isUnit(typeParameterResolver) == true
    }

    private fun KSFunctionDeclarationWrapper.isCoroutineCommand(): Boolean {
        return modifiers.contains(Modifier.SUSPEND)
    }

    private fun KSFunctionDeclarationWrapper.isFlowCommand(): Boolean {
        return returnType?.isKotlinFlow(typeParameterResolver) == true
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
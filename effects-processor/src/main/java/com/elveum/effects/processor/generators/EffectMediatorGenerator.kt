package com.elveum.effects.processor.generators

import com.elveum.effects.processor.data.Const
import com.elveum.effects.processor.data.EffectMetadata
import com.elveum.effects.processor.exceptions.CleanUpFunctionIsAbstractException
import com.elveum.effects.processor.exceptions.NonDefaultCleanUpMethodIsNotSpecifiedException
import com.elveum.effects.processor.exceptions.NonUnitCleanUpFunctionException
import com.elveum.effects.processor.exceptions.UnitCommandWithReturnTypeException
import com.elveum.effects.processor.extensions.KSFunctionDeclarationWrapper
import com.elveum.effects.processor.extensions.implementInterface
import com.elveum.effects.processor.extensions.implementInterfaceMethod
import com.elveum.effects.processor.extensions.primaryConstructorWithProperties
import com.elveum.effects.processor.generators.base.KspClassWriter
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
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
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver

class EffectMediatorGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(effectMetadata: EffectMetadata): Result {
        val interfaceDeclaration = effectMetadata.targetInterfaceDeclaration
        val interfaceClassName = interfaceDeclaration.toClassName()
        val interfaceName = interfaceClassName.simpleName
        val mediatorName = "__${interfaceName}Mediator"
        val mediatorClassName = ClassName(interfaceClassName.packageName, mediatorName)

        val typeSpecBuilder = TypeSpec.classBuilder(mediatorClassName)
            .addConstructor(interfaceClassName)
            .implementInterface(interfaceDeclaration, ::implementMethod)
            .apply {
                val cleanUpMethodDeclaration = findCleanUpMethod(effectMetadata)
                if (cleanUpMethodDeclaration != null) {
                    implementCleanUpMethod(cleanUpMethodDeclaration)
                }
            }
            .addInternalCleanUpMethod()

        writer.write(
            typeSpec = typeSpecBuilder.build(),
            pkg = mediatorClassName.packageName,
            dependencies = effectMetadata.dependencies,
        )

        return Result(
            mediatorClassName = mediatorClassName,
        )
    }

    private fun TypeSpec.Builder.addConstructor(interfaceClassName: ClassName): TypeSpec.Builder {
        return primaryConstructorWithProperties(
            FunSpec.constructorBuilder()
                .addAnnotation(Const.JavaxInjectAnnotationName)
                .addParameter(COMMAND_EXECUTOR_PROPERTY, Const.commandExecutorName(interfaceClassName))
                .build()
        )
    }

    private fun TypeSpec.Builder.implementCleanUpMethod(
        cleanUpMethodDeclaration: KSFunctionDeclaration,
    ): TypeSpec.Builder {
        val typeParameterResolver = cleanUpMethodDeclaration.typeParameters.toTypeParameterResolver()
        val funBuilder = implementInterfaceMethod(cleanUpMethodDeclaration, typeParameterResolver)
        return addFunction(
            funBuilder
                .addCode("$INTERNAL_CLEAN_UP_METHOD()")
                .build()
        )
    }

    private fun TypeSpec.Builder.addInternalCleanUpMethod(): TypeSpec.Builder {
        return addFunction(
            FunSpec.builder(INTERNAL_CLEAN_UP_METHOD)
                .addCode("$COMMAND_EXECUTOR_PROPERTY.cleanUp()")
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
        val lambdaCode = "it.${function.simpleNameString}($params)"
        val commandType = getCommandType(function)
        return generateCommandMethod(commandType, lambdaCode)
    }

    private fun generateCommandMethod(
        commandType: CommandType,
        lambdaCode: String,
    ): CodeBlock {
        val commandExecutorMethodName = commandType.commandExecutorMethodName
        val returnStatement = if (commandType == CommandType.UnitCommand) {
            ""
        } else {
            "return "
        }
        return CodeBlock.of("« $returnStatement$COMMAND_EXECUTOR_PROPERTY.$commandExecutorMethodName {\n$lambdaCode\n» }") //
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

    private fun findCleanUpMethod(metadata: EffectMetadata): KSFunctionDeclaration? {
        val cleanUpMethodName = metadata.cleanUpMethodName
        return metadata.targetInterfaceDeclaration.getAllFunctions()
            .firstOrNull { functionDeclaration ->
                functionDeclaration.simpleName.asString() == cleanUpMethodName.simpleText
            }
            .let { assertCleanUpFunctionDeclaration(metadata, it) }
    }

    private fun assertCleanUpFunctionDeclaration(
        metadata: EffectMetadata,
        cleanUpFunctionDeclaration: KSFunctionDeclaration?,
    ): KSFunctionDeclaration? {
        if (cleanUpFunctionDeclaration == null ) {
            if (metadata.cleanUpMethodName.isDefaultCleanUpMethodName) {
                return null
            } else {
                throw NonDefaultCleanUpMethodIsNotSpecifiedException(metadata)
            }
        }
        val typeParameterResolver = cleanUpFunctionDeclaration.typeParameters.toTypeParameterResolver()
        if (cleanUpFunctionDeclaration.returnType?.isUnit(typeParameterResolver) != true) {
            throw NonUnitCleanUpFunctionException(cleanUpFunctionDeclaration)
        }
        if (cleanUpFunctionDeclaration.isAbstract) {
            throw CleanUpFunctionIsAbstractException(cleanUpFunctionDeclaration)
        }
        return cleanUpFunctionDeclaration
    }

    enum class CommandType(
        val commandExecutorMethodName: String
    ) {
        UnitCommand("execute"),
        CoroutineCommand("executeCoroutine"),
        FlowCommand("executeFlow");
    }

    class Result(
        val mediatorClassName: ClassName,
        val internalCleanUpMethodName: String = INTERNAL_CLEAN_UP_METHOD,
    )

    companion object {
        private const val COMMAND_EXECUTOR_PROPERTY = "commandExecutor"
        private const val INTERNAL_CLEAN_UP_METHOD = "__internalCleanUp"
    }
}

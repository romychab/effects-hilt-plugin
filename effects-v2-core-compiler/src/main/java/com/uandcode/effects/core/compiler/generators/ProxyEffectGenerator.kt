package com.uandcode.effects.core.compiler.generators

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
import com.uandcode.effects.core.compiler.api.KspClassWriter
import com.uandcode.effects.core.compiler.api.data.GeneratedProxy
import com.uandcode.effects.core.compiler.api.data.ParsedMetadata
import com.uandcode.effects.core.compiler.api.extensions.KSFunctionDeclarationWrapper
import com.uandcode.effects.core.compiler.api.extensions.implementInterface
import com.uandcode.effects.core.compiler.api.extensions.implementInterfaceMethod
import com.uandcode.effects.core.compiler.api.extensions.primaryConstructorWithProperties
import com.uandcode.effects.core.compiler.Const
import com.uandcode.effects.core.compiler.exceptions.CleanUpFunctionHasArgumentsException
import com.uandcode.effects.core.compiler.exceptions.NonDefaultCleanUpMethodIsNotSpecifiedException
import com.uandcode.effects.core.compiler.exceptions.NonUnitCleanUpFunctionException
import com.uandcode.effects.core.compiler.exceptions.UnitCommandWithReturnTypeException

internal class ProxyEffectGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(metadata: ParsedMetadata): GeneratedProxy {
        val interfaceDeclaration = metadata.interfaceDeclaration
        val interfaceClassName = interfaceDeclaration.toClassName()
        val interfaceName = interfaceClassName.simpleName
        val proxyName = "__${interfaceName}Proxy"
        val proxyClassName = ClassName(interfaceClassName.packageName, proxyName)

        val typeSpecBuilder = TypeSpec.classBuilder(proxyClassName)
            .addConstructor(interfaceClassName)
            .implementInterface(interfaceDeclaration, ::implementMethod)
            .implementAutoCloseable()
            .apply {
                val cleanUpMethodDeclaration = findCleanUpMethod(metadata)
                if (cleanUpMethodDeclaration != null
                        && cleanUpMethodDeclaration.simpleName.asString() != AUTO_CLOSE_METHOD) {
                    implementCleanUpMethod(cleanUpMethodDeclaration)
                }
            }
            .addInternalCleanUpMethod()

        writer.write(
            typeSpec = typeSpecBuilder.build(),
            pkg = proxyClassName.packageName,
            dependencies = metadata.dependencies,
        )

        return GeneratedProxy(
            proxyClassName = proxyClassName,
            interfaceClassName = interfaceClassName,
            dependencies = metadata.dependencies.originatingFiles,
        )
    }

    private fun TypeSpec.Builder.implementAutoCloseable() = apply {
        addSuperinterface(ClassName("kotlin", "AutoCloseable"))
        addFunction(
            FunSpec.builder(AUTO_CLOSE_METHOD)
                .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
                .addCode("${INTERNAL_CLEAN_UP_METHOD}()")
                .build()
        )
    }

    private fun findCleanUpMethod(metadata: ParsedMetadata): KSFunctionDeclaration? {
        val cleanUpMethodName = metadata.cleanUpMethodName
        return metadata.interfaceDeclaration.getAllFunctions()
            .firstOrNull { functionDeclaration ->
                functionDeclaration.simpleName.asString() == cleanUpMethodName.simpleText
                        && !functionDeclaration.isAbstract
            }
            .let { assertCleanUpFunctionDeclaration(metadata, it) }
    }

    private fun TypeSpec.Builder.addConstructor(interfaceClassName: ClassName): TypeSpec.Builder {
        return primaryConstructorWithProperties(
            FunSpec.constructorBuilder()
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
                .addCode("${INTERNAL_CLEAN_UP_METHOD}()")
                .build()
        )
    }

    private fun TypeSpec.Builder.addInternalCleanUpMethod(): TypeSpec.Builder {
        return addFunction(
            FunSpec.builder(INTERNAL_CLEAN_UP_METHOD)
                .addCode("${COMMAND_EXECUTOR_PROPERTY}.cleanUp()")
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
        return CodeBlock.of("« $returnStatement${COMMAND_EXECUTOR_PROPERTY}.$commandExecutorMethodName {\n$lambdaCode\n» }") //
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

    private fun assertCleanUpFunctionDeclaration(
        metadata: ParsedMetadata,
        cleanUpFunctionDeclaration: KSFunctionDeclaration?,
    ): KSFunctionDeclaration? {
        if (cleanUpFunctionDeclaration == null) {
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
        if (cleanUpFunctionDeclaration.parameters.isNotEmpty()) {
            throw CleanUpFunctionHasArgumentsException(cleanUpFunctionDeclaration)
        }
        return cleanUpFunctionDeclaration
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

    enum class CommandType(
        val commandExecutorMethodName: String
    ) {
        UnitCommand("execute"),
        CoroutineCommand("executeCoroutine"),
        FlowCommand("executeFlow");
    }

    companion object {
        private const val COMMAND_EXECUTOR_PROPERTY = "commandExecutor"
        private const val INTERNAL_CLEAN_UP_METHOD = "__internalCleanUp"
        private const val AUTO_CLOSE_METHOD = "close"
    }
}

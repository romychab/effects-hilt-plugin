package com.uandcode.effects.compiler.common.generators

import com.google.devtools.ksp.getAllSuperTypes
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
import com.uandcode.effects.compiler.common.Const
import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.GeneratedProxy
import com.uandcode.effects.compiler.common.api.data.ParsedMetadata
import com.uandcode.effects.compiler.common.api.data.aggregateDependencies
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.compiler.common.api.extensions.KSFunctionDeclarationWrapper
import com.uandcode.effects.compiler.common.api.extensions.implementInterface
import com.uandcode.effects.compiler.common.api.extensions.primaryConstructorWithProperties
import com.uandcode.effects.compiler.common.exceptions.AutoCloseNotImplementedException
import com.uandcode.effects.compiler.common.exceptions.CloseMethodWithoutAutoCloseableException
import com.uandcode.effects.compiler.common.exceptions.UnitCommandWithReturnTypeException

internal class ProxyEffectGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(
        interfaceDeclaration: KSClassDeclarationWrapper,
        metadata: List<ParsedMetadata>,
    ): GeneratedProxy {
        val interfaceClassName = interfaceDeclaration.toClassName()
        val interfaceName = interfaceClassName.simpleName
        val proxyName = "__${interfaceName}Proxy"
        val proxyClassName = ClassName(interfaceClassName.packageName, proxyName)

        val typeSpecBuilder = TypeSpec.classBuilder(proxyClassName)
            .addConstructor(interfaceClassName)
            .addSuperinterface(Const.EffectProxyMarker)
            .implementInterface(
                interfaceDeclaration = interfaceDeclaration,
                filter = { function -> !isCloseMethod(function) },
                functionBody = ::implementMethod,
            )
            .implementAutoCloseable(interfaceDeclaration)

        val dependencies = metadata.aggregateDependencies()
        writer.write(
            typeSpec = typeSpecBuilder.build(),
            pkg = proxyClassName.packageName,
            dependencies = dependencies,
        )

        return GeneratedProxy(
            proxyClassName = proxyClassName,
            interfaceClassName = interfaceClassName,
            dependencies = dependencies,
        )
    }

    private fun TypeSpec.Builder.implementAutoCloseable(
        interfaceDeclaration: KSClassDeclarationWrapper,
    ) = apply {
        if (interfaceDeclaration.hasAutoCloseableInterface()) {
            val hasImplementedCloseMethod = interfaceDeclaration.getAllFunctions()
                .any { isCloseMethod(it) && !it.isAbstract && it.modifiers.contains(Modifier.OVERRIDE) }
            if (!hasImplementedCloseMethod) throw AutoCloseNotImplementedException(interfaceDeclaration)
        } else {
            val hasCloseMethod = interfaceDeclaration.getAllFunctions().any { isCloseMethod(it) }
            if (hasCloseMethod) throw CloseMethodWithoutAutoCloseableException(interfaceDeclaration)
            addSuperinterface(Const.AutoCloseableClassName)
        }
        return addFunction(
            FunSpec.builder("close")
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .addCode("${COMMAND_EXECUTOR_PROPERTY}.cleanUp()")
                .build()
        )
    }

    private fun TypeSpec.Builder.addConstructor(interfaceClassName: ClassName): TypeSpec.Builder {
        return primaryConstructorWithProperties(
            FunSpec.constructorBuilder()
                .addParameter(COMMAND_EXECUTOR_PROPERTY, Const.commandExecutorName(interfaceClassName))
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

    private fun KSClassDeclarationWrapper.hasAutoCloseableInterface(): Boolean {
        return getAllSuperTypes().any {
            val typeName = it.toTypeName()
            typeName == Const.AutoCloseableClassName
                    || typeName == Const.AutoCloseableJavaClassName
        }
    }

    private fun isCloseMethod(
        function: KSFunctionDeclaration,
    ): Boolean {
        return function.simpleName.asString() == AUTO_CLOSE_METHOD
                && function.parameters.isEmpty()
                && function.typeParameters.isEmpty()
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
        private const val AUTO_CLOSE_METHOD = "close"
    }
}

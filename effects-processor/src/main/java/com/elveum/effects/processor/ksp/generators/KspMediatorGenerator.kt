package com.elveum.effects.processor.ksp.generators

import com.elveum.effects.processor.KNames
import com.elveum.effects.processor.Qualifier
import com.elveum.effects.processor.ksp.KspElementException
import com.elveum.effects.processor.ksp.KspNames
import com.elveum.effects.processor.ksp.KspParsedElements
import com.elveum.effects.processor.ksp.isQualifier
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName

class KspMediatorGenerator(
    private val codeGenerator: CodeGenerator,
) {

    fun generateMediator(annotatedClass: KSClassDeclaration): Result {
        if (annotatedClass.typeParameters.isNotEmpty()) {
            throw KspElementException("Class annotated with @SideEffect should not have type parameters", annotatedClass)
        }
        val parsedElements = parseInterface(annotatedClass)
        val mediatorClassName = ClassName(parsedElements.pkg, parsedElements.suggestedMediatorName)
        val mediatorClassBuilder = TypeSpec.classBuilder(mediatorClassName)
        val interfaceTypeName = parsedElements.directInterface.toClassName()
        with(mediatorClassBuilder) {
            addModifiers(KModifier.PUBLIC)
            addSuperinterface(interfaceTypeName)
            addSuperinterface(KNames.sideEffectMediator(interfaceTypeName))
            generateFields(interfaceTypeName)
            generateConstructor()
            implementInterfaceMethods(interfaceTypeName, parsedElements)
        }

        val mediatorTypeSpec = mediatorClassBuilder.build()
        val writer = KspClassWriter(codeGenerator, parsedElements)
        writer.write(
            name = parsedElements.suggestedMediatorName,
            typeSpec = mediatorTypeSpec
        )
        return Result(
            parsedElements = parsedElements,
            generatedMediatorTypeSpec = mediatorTypeSpec,
            writer = KspClassWriter(codeGenerator, parsedElements)
        )
    }

    private fun parseInterface(annotatedClass: KSClassDeclaration): KspParsedElements {
        if (annotatedClass.modifiers.contains(Modifier.ABSTRACT)) {
            throw KspElementException("Class annotated with @SideEffect should not be an abstract class", annotatedClass)
        }
        val allInterfaces = annotatedClass.superTypes
            .map { it.resolve().declaration }
            .filterIsInstance<KSClassDeclaration>()
            .filter { isInterface(it) }
            .toList()
        val targetInterface = findTargetInterface(annotatedClass, allInterfaces)
        if (targetInterface.typeParameters.isNotEmpty()) {
            throw KspElementException("Target interface shouldn't have generic type parameters", targetInterface)
        }

        val qualifierAnnotation = annotatedClass.annotations.firstOrNull { annotation ->
            annotation.isQualifier()
        }
        val qualifier = qualifierAnnotation?.let {
            val annotationType = it.annotationType.resolve()
            val annotationDeclaration = annotationType.declaration
            Qualifier(
                pkg = annotationDeclaration.packageName.asString(),
                name = annotationDeclaration.simpleName.getShortName(),
            )
        }

        return KspParsedElements(
            qualifier = qualifier,
            origin = annotatedClass,
            directInterface = targetInterface,
            pkg = annotatedClass.packageName.asString(),
            methods = targetInterface.getAllFunctions()
                .filter { it.isAbstract }
                .toList(),
            originName = annotatedClass.simpleName.asString(),
            suggestedMediatorName = "GeneratedMediator${annotatedClass.simpleName.asString()}",
            otherInterfaces = allInterfaces.filter { it !== targetInterface }
        )
    }

    private fun findTargetInterface(
        annotatedClass: KSClassDeclaration,
        allInterfaces: List<KSClassDeclaration>,
    ): KSClassDeclaration {
        if (allInterfaces.isEmpty()) {
            throw KspElementException("Class annotated with @SideEffect doesn't implement any interface", annotatedClass)
        }

        return if (allInterfaces.size == 1) {
            allInterfaces.first()
        } else {
            findTargetInterfaceFromAnnotation(annotatedClass, allInterfaces)
        }

    }

    private fun isInterface(ref: KSClassDeclaration): Boolean {
        return ref.classKind == ClassKind.INTERFACE
    }

    private fun findTargetInterfaceFromAnnotation(
        annotatedClass: KSClassDeclaration,
        interfaces: List<KSClassDeclaration>,
    ): KSClassDeclaration {
        val sideEffectAnnotation = annotatedClass.annotations.first {
            it.shortName.getShortName() == KspNames.sideEffectShortAnnotationName
        }
        val targetArg = sideEffectAnnotation.arguments.firstOrNull()
            ?: throwTargetException(annotatedClass)
        val targetValue = targetArg.value as KSType

        val targetInterface = interfaces.firstOrNull {
            it.qualifiedName?.asString() == targetValue.declaration.qualifiedName?.asString()
        } ?: throw KspElementException("@SideEffect target argument must " +
                "point to an interface which is implemented by your class", annotatedClass)

        return targetInterface
    }

    private fun throwTargetException(annotatedClass: KSClassDeclaration): Nothing {
        throw KspElementException("Your class annotated with @SideEffect implements " +
                "more than 1 interface. Please specify @SideEffect(target=...) parameter.", annotatedClass)
    }

    private fun TypeSpec.Builder.generateFields(interfaceTypeName: TypeName) {
        addProperty(PropertySpec.builder("target", interfaceTypeName.copy(nullable = true))
            .mutable()
            .getter(FunSpec.getterBuilder()
                .addCode("return commandProcessor.resource")
                .build())
            .setter(FunSpec.setterBuilder()
                .addParameter("newValue", interfaceTypeName)
                .addCode("commandProcessor.resource = newValue")
                .build())
            .addModifiers(KModifier.OVERRIDE)
            .build())
        addProperty(PropertySpec.builder("commandProcessor", KNames.commandProcessor(interfaceTypeName))
            .initializer("CommandProcessor(scope)")
            .addModifiers(KModifier.PRIVATE)
            .build())
    }

    private fun TypeSpec.Builder.generateConstructor() {
        primaryConstructor(FunSpec.constructorBuilder()
            .addParameter("scope", KNames.coroutineScope)
            .build())
    }

    private fun TypeSpec.Builder.implementInterfaceMethods(
        interfaceTypeName: TypeName,
        parsedElements: KspParsedElements,
    ) {
        parsedElements.methods
            .forEach { interfaceMethod ->
                implementMethod(interfaceTypeName, interfaceMethod)
            }
    }

    private fun TypeSpec.Builder.implementMethod(
        interfaceTypeName: TypeName,
        interfaceMethod: KSFunctionDeclaration,
    ) {
        val typeResolver = interfaceMethod.typeParameters.toTypeParameterResolver()
        val returnTypeName = interfaceMethod.returnType!!.toTypeName(typeResolver)
        if (!returnTypeName.isUnit()
            && !isFlow(returnTypeName)
            && !isCoroutine(interfaceMethod)) {
            throw KspElementException("Only suspend and Flow methods can have a return type", interfaceMethod)
        }
        val funBuilder = FunSpec.builder(interfaceMethod.simpleName.getShortName())
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)

        funBuilder.addParameters(interfaceMethod.parameters.map {
            val name = it.name!!.getShortName()
            val ksType = it.type.toTypeName(typeResolver)
            return@map ParameterSpec(name, ksType)
        })
        funBuilder.addTypeVariables(interfaceMethod.typeParameters.map { typeParameter ->
            typeParameter.toTypeVariableName(typeResolver)
        })
        if (interfaceMethod.modifiers.contains(Modifier.SUSPEND)) {
            funBuilder.addModifiers(KModifier.SUSPEND)
        }

        funBuilder.returns(returnTypeName)

        if (isCoroutine(interfaceMethod)) {
            funBuilder.generateCoroutineCommand(interfaceTypeName, interfaceMethod, returnTypeName)
        } else if (isFlow(returnTypeName)) {
            funBuilder.generateFlowCommand(interfaceTypeName, interfaceMethod, returnTypeName)
        } else {
            funBuilder.generateUnitCommand(interfaceTypeName, interfaceMethod)
        }

        addFunction(funBuilder.build())

    }

    private fun isCoroutine(functionDeclaration: KSFunctionDeclaration): Boolean {
        return functionDeclaration.modifiers.contains(Modifier.SUSPEND)
    }

    private fun isFlow(typeName: TypeName): Boolean {
        return typeName is ParameterizedTypeName
                && typeName.rawType.reflectionName() == KspNames.flow
    }

    private fun FunSpec.Builder.generateUnitCommand(
        interfaceTypeName: TypeName,
        interfaceMethod: KSFunctionDeclaration
    ) {
        val params = parameters.map { it.name }.joinToString(", ")
        val methodName = interfaceMethod.simpleName.getShortName()
        addCode("val command: %T = ", KNames.unitCommand(interfaceTypeName))
        addCode("""
            |{ 
            |  it.$methodName($params) 
            |}
            |commandProcessor.submit(command)
        """.trimMargin())
    }

    private fun FunSpec.Builder.generateCoroutineCommand(
        interfaceTypeName: TypeName,
        interfaceMethod: KSFunctionDeclaration,
        kReturnType: TypeName
    ) {
        val params = parameters.joinToString(", ") { it.name }
        val methodName = interfaceMethod.simpleName.getShortName()
        addCode("val command: %T = ", KNames.coroutineCommand(interfaceTypeName, kReturnType))
        addCode("""
            |{ 
            |  it.$methodName($params) 
            |}
            |return commandProcessor.submitCoroutine(command)
        """.trimMargin())
    }

    private fun FunSpec.Builder.generateFlowCommand(
        interfaceTypeName: TypeName,
        interfaceMethod: KSFunctionDeclaration,
        kReturnType: TypeName
    ) {
        val innerType = (kReturnType as ParameterizedTypeName).typeArguments.first()
        val params = parameters.joinToString(", ") { it.name }
        val methodName = interfaceMethod.simpleName.getShortName()
        addCode("val command: %T = ", KNames.flowCommand(interfaceTypeName, innerType))
        addCode("""
            |{ 
            |  it.$methodName($params) 
            |}
            |return commandProcessor.submitFlow(command)
        """.trimMargin())
    }

    private fun TypeName.isUnit(): Boolean {
        return this is ClassName &&
                this.reflectionName() == "kotlin.Unit"
    }

    class Result(
        val parsedElements: KspParsedElements,
        val generatedMediatorTypeSpec: TypeSpec,
        val writer: KspClassWriter,
    )
}

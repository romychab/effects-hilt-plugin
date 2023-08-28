package com.elveum.effects.processor.generators

import com.elveum.effects.annotations.SideEffect
import com.elveum.effects.processor.*
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.WildcardTypeName
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.javapoet.JTypeName
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKTypeName
import javax.annotation.processing.Filer
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * This generator creates an intermediate implementation (mediator) of the side-effect
 * interface which is injected to the view-model constructor.
 *
 * Output name of the generated class: `GeneratedMediator{ImplementationClassName}`.
 */
@KotlinPoetJavaPoetPreview
class KotlinMediatorGenerator(
    private val types: Types,
    private val elements: Elements,
    private val filer: Filer,
) {

    fun generateMediatorClass(element: TypeElement): Result {
        val parsedElements = parseInterface(element)
        val mediatorClassName = ClassName(parsedElements.pkg, parsedElements.suggestedMediatorName)
        val mediatorClassBuilder = TypeSpec.classBuilder(mediatorClassName)
        val interfaceTypeName = parsedElements.directInterface.asClassName()
        with(mediatorClassBuilder) {
            addModifiers(KModifier.PUBLIC)
            addSuperinterface(interfaceTypeName)
            addSuperinterface(KNames.sideEffectMediator(interfaceTypeName))
            generateFields(interfaceTypeName)
            generateConstructor()
            implementInterfaceMethods(interfaceTypeName, parsedElements)
        }

        val result = mediatorClassBuilder.build()
        val file = FileSpec.builder(parsedElements.pkg, parsedElements.suggestedMediatorName)
            .addType(result)
            .build()
        file.writeTo(filer)
        return Result(parsedElements, result)
    }

    private fun TypeSpec.Builder.implementInterfaceMethods(
        interfaceTypeName: TypeName,
        parsedElements: ParsedElements
    ) {
        parsedElements.methods
            .forEach { interfaceMethod ->
                implementMethod(interfaceTypeName, interfaceMethod)
            }
    }

    private fun TypeSpec.Builder.implementMethod(
        interfaceTypeName: TypeName,
        interfaceMethod: ExecutableElement
    ) {
        val returnType = interfaceMethod.returnType
        if (returnType.toString() != "void"
            && !isFlow(interfaceMethod)
            && !isCoroutine(interfaceMethod)) {
            throw ElementException("Only suspend and Flow methods can have a return type", interfaceMethod)
        }
        val funBuilder = FunSpec.builder(interfaceMethod.simpleName.toString())
            .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)

        val params: Iterable<VariableElement> = if (isCoroutine(interfaceMethod)) {
            interfaceMethod.parameters.subList(0, interfaceMethod.parameters.size - 1)
        } else {
            interfaceMethod.parameters
        }
        funBuilder.addParameters(params.map {
            val typeMirror = it.asType()
            val className = com.squareup.javapoet.ClassName.get(typeMirror)
            ParameterSpec(it.simpleName.toString(), className.toKTypeName())
        })
        funBuilder.addTypeVariables(interfaceMethod.typeParameters.map {
            if (it.bounds.isEmpty() ||
                (it.bounds.size == 1 && com.squareup.javapoet.ClassName.get(it.bounds[0]) == com.squareup.javapoet.ClassName.OBJECT)) {
                TypeVariableName(it.simpleName.toString())
            } else {
                TypeVariableName(
                    it.simpleName.toString(),
                    it.bounds.map { mirror ->
                        com.squareup.javapoet.TypeName.get(mirror).toKTypeName()
                    }
                )
            }
        })
        if (isCoroutine(interfaceMethod)) {
            funBuilder.addModifiers(KModifier.SUSPEND)
            val lastParam = interfaceMethod.parameters.last()
            val lastType = lastParam.asType()
            val jTypeNames = JTypeName.get(lastType) as ParameterizedTypeName
            val jFirstTypeName = jTypeNames.typeArguments[0]
            val kReturnType = (jFirstTypeName as WildcardTypeName).lowerBounds[0].toKTypeName()
            if (kReturnType is com.squareup.kotlinpoet.ParameterizedTypeName) {
                if (kReturnType.rawType == KNames.rawFlow) {
                    throw ElementException("Flow can be returned only by non-suspend methods", interfaceMethod)
                }
            }
            funBuilder.returns(kReturnType)
            funBuilder.generateCoroutineCommand(interfaceTypeName, interfaceMethod, kReturnType)
        } else if (isFlow(interfaceMethod)) {
            val flowTypeMirror = interfaceMethod.returnType
            val flowTypeName = com.squareup.javapoet.TypeName.get(flowTypeMirror) as ParameterizedTypeName
            val kReturnType = flowTypeName.typeArguments.first().toKTypeName()
            funBuilder.generateFlowCommand(interfaceTypeName, interfaceMethod, kReturnType)
            funBuilder.returns(KNames.flow(kReturnType))
        } else {
            funBuilder.generateUnitCommand(interfaceTypeName, interfaceMethod)
        }

        addFunction(funBuilder.build())

    }

    private fun FunSpec.Builder.generateUnitCommand(
        interfaceTypeName: TypeName,
        interfaceMethod: ExecutableElement
    ) {
        val params = parameters.map { it.name }.joinToString(", ")
        val methodName = interfaceMethod.simpleName.toString()
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
        interfaceMethod: ExecutableElement,
        kReturnType: TypeName
    ) {
        val params = parameters.joinToString(", ") { it.name }
        val methodName = interfaceMethod.simpleName.toString()
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
        interfaceMethod: ExecutableElement,
        kReturnType: TypeName
    ) {
        val params = parameters.joinToString(", ") { it.name }
        val methodName = interfaceMethod.simpleName.toString()
        addCode("val command: %T = ", KNames.flowCommand(interfaceTypeName, kReturnType))
        addCode("""
            |{ 
            |  it.$methodName($params) 
            |}
            |return commandProcessor.submitFlow(command)
        """.trimMargin())
    }

    private fun TypeSpec.Builder.generateConstructor() {
        primaryConstructor(FunSpec.constructorBuilder()
            .addParameter("scope", KNames.coroutineScope)
            .build())
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

    private fun parseInterface(element: TypeElement): ParsedElements {
        if (element.modifiers.contains(Modifier.ABSTRACT))
            throw ElementException("Class annotated with @SideEffect should not be an abstract class", element)

        val targetInterface = findTargetInterface(element)
        val otherInterfaces = element.interfaces
            .map { types.asElement(it) as TypeElement }
            .filter { it.simpleName.toString() != targetInterface.simpleName.toString() }

        if (otherInterfaces.any { it.typeParameters.isNotEmpty() }) {
            throw ElementException("Class annotated with @SideEffect should not " +
                    "implement interfaces with generic type parameters", element)
        }

        val originName = element.simpleName
        val modifiedName = "GeneratedMediator${originName}"
        val qualifierAnnotationMirror = element.annotationMirrors.firstOrNull { annotationMirror ->
            val annotationElement = annotationMirror.annotationType.asElement() as TypeElement
            annotationElement.annotationMirrors.any {
                it.annotationType.asTypeName() == KNames.qualifier
            }
        }
        val qualifier = qualifierAnnotationMirror?.let {
            val annotationType = it.annotationType.asElement()
            Qualifier(
                pkg = elements.getPackageOf(annotationType).toString(),
                name = annotationType.simpleName.toString()
            )
        }

        return ParsedElements(
            qualifier = qualifier,
            origin = element,
            originName = element.simpleName.toString(),
            pkg = elements.getPackageOf(element).toString(),
            directInterface = targetInterface,
            otherInterfaces = otherInterfaces,
            suggestedMediatorName = modifiedName,
            methods = targetInterface.enclosedElements
                .filterIsInstance<ExecutableElement>()
                .toSet()
        )
    }

    private fun findTargetInterface(element: TypeElement): TypeElement {
        if (element.interfaces.isEmpty())
            throw ElementException("Class annotated with @SideEffect doesn't implement any interface", element)
        val targetInterface: TypeElement
        if (element.interfaces.size == 1) {
            targetInterface = types.asElement(element.interfaces[0]) as TypeElement
        } else {
            targetInterface = findTargetElement(element)
        }
        if (targetInterface.typeParameters.isNotEmpty()) {
            throw ElementException("Interface shouldn't have generic type parameters", targetInterface)
        }
        return targetInterface
    }

    private fun findTargetElement(element: TypeElement): TypeElement {
        val annotationMirror = element.annotationMirrors.firstOrNull {
            val annotationElement = it.annotationType.asElement()
            if (annotationElement !is TypeElement) return@firstOrNull false
            if (annotationElement.qualifiedName.toString() != SideEffect::class.java.name)
                return@firstOrNull false
            true
        } ?: throw ElementException("Can't find @SideEffect annotation", element)

        val args = annotationMirror.elementValues
        if (args.isEmpty()) throwTargetException(element)
        val value = (args.values.firstOrNull()?.value as? DeclaredType)
            ?.asElement() as? TypeElement ?: throwTargetException(element)
        if (value.qualifiedName.toString() == "java.lang.Object") throwTargetException(element)
        return elements.getTypeElement(value.qualifiedName)
    }

    private fun throwTargetException(element: TypeElement): Nothing {
        throw ElementException("Your class annotated with @SideEffect implements " +
                "more than 1 interface. Please specify @SideEffect(target=...) parameter.", element)
    }

    private fun isCoroutine(executableElement: ExecutableElement): Boolean {
        val parameters = executableElement.parameters
        if (parameters.isEmpty()) return false
        val last = parameters.last()
        val paramTypeName = com.squareup.javapoet.TypeName.get(last.asType())
        if (paramTypeName is ParameterizedTypeName) {
            if (paramTypeName.typeArguments.size != 1) return false
            val expectedTypeName = Names.continuation(
                paramTypeName.typeArguments[0]
            )
            return paramTypeName == expectedTypeName
        }
        return false
    }

    private fun isFlow(executableElement: ExecutableElement): Boolean {
        val returnTypeMirror = executableElement.returnType
        val returnTypeName = com.squareup.javapoet.TypeName.get(returnTypeMirror)
        if (returnTypeName is ParameterizedTypeName) {
            val rawType = returnTypeName.rawType
            return rawType == Names.flow
        }
        return false
    }

    class Result(
        val parsedElements: ParsedElements,
        val typeSpec: TypeSpec
    )
}
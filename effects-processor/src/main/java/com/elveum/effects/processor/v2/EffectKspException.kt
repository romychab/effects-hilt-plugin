package com.elveum.effects.processor.v2

import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper
import com.elveum.effects.processor.v2.extensions.KSClassDeclarationWrapper
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ksp.toClassName

abstract class EffectKspException(
    message: String,
    val node: KSNode,
) : Exception(message)

class MultipleEffectImplementationsException(
    targetInterface: KSClassDeclarationWrapper,
    allImplementations: List<KSClassDeclarationWrapper>,
) : EffectKspException(
    message = "Target interface '${targetInterface.simpleNameText}' should have only one implementation. " +
            "Current implementations: " + allImplementations.joinToString(", ") { it.simpleNameText },
    node = targetInterface,
)

class InvalidClassTypeException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : EffectKspException(
    message = "Symbol annotated with @${effectAnnotation.simpleName} should be a Class or Object",
    node,
)

class ClassIsAbstractException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : EffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should not be abstract",
    node,
)

class ClassWithTypeParametersException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : EffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should not have type parameters",
    node,
)

class InterfaceWithTypeParametersException(
    effectAnnotation: KSAnnotationWrapper,
    targetInterfaceName: String,
    node: KSNode,
) : EffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should not have a target interface '$targetInterfaceName' with type parameters",
    node,
)

class NestedClassException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : EffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should be a top-level class",
    node,
)

class NestedInterfaceException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : EffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should not implement nested interface",
    node,
)

class InvalidTargetInterfaceException(
    allowedInterfaces: List<KSClassDeclaration>,
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : EffectKspException(
    message = "@${effectAnnotation.simpleName}(target = ...) parameter can be set only to these values: " +
            allowedInterfaces.joinToString(", ") { "${it.toClassName().simpleName}::class" } ,
    node,
)

class ClassDoesNotImplementInterfaceException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : EffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should implement at least one interface",
    node,
)

class TargetInterfaceIsNotSpecifiedException(
    effectAnnotation: KSAnnotationWrapper,
) : EffectKspException(
    message = "${effectAnnotation.printableName}(target = ...) parameter should be specified if your class " +
            "implements more than 1 interface",
    effectAnnotation,
)

class UnitCommandWithReturnTypeException(
    function: KSFunctionDeclaration,
) : EffectKspException(
    "Non-suspend methods can't have a return type",
    function,
)

class InvalidHiltComponentException(
    effectAnnotation: KSAnnotationWrapper,
) : EffectKspException(
    "Invalid Hilt Component is specified in ${effectAnnotation.printableName}(installIn = ...). " +
            "Example of valid Hilt components: SingletonComponent::class, ActivityRetainedComponent::class, ActivityComponent::class, etc...",
    effectAnnotation,
)

/**
 * This exception can't be thrown in real scenarios
 */
class InvalidTargetArgumentException(
    effectAnnotation: KSAnnotationWrapper,
) : EffectKspException(
    message = "${effectAnnotation.printableName}(target = ...) has invalid target parameter value.",
    effectAnnotation,
)

/**
 * This exception can't be thrown in real scenarios
 */
class InvalidInstallInArgumentException(
    effectAnnotation: KSAnnotationWrapper,
) : EffectKspException(
    message = "${effectAnnotation.printableName}(installIn = ...) has invalid parameter value.",
    effectAnnotation,
)

/**
 * Internal exception in the plugin logic.
 */
class InternalEffectKspException(
    message: String,
) : Exception(message)
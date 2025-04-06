package com.uandcode.effects.core.compiler.api.data

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.effects.core.compiler.Const
import com.uandcode.effects.core.compiler.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.core.compiler.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.core.compiler.exceptions.ClassDoesNotImplementInterfaceException
import com.uandcode.effects.core.compiler.exceptions.InvalidTargetInterfaceException
import com.uandcode.effects.core.compiler.exceptions.TargetInterfaceIsNotSpecifiedException

/**
 * Open class that represents a parsed effect. Subclasses can be created
 * in plugins that extend this core compiler.
 */
public open class ParsedEffect(
    public val classDeclaration: KSClassDeclarationWrapper,
    public val annotationClassName: ClassName = EffectClass::class.asClassName(),
    public val targetArgument: String = Const.TargetArgument,
) {

    public val className: ClassName = classDeclaration.toClassName()
    public val simpleName: String = className.simpleName

    public val targetInterface: KSClassDeclarationWrapper by lazy {
        findTargetInterfaceClassDeclaration()
    }
    public val targetInterfaceClassName: ClassName by lazy { targetInterface.toClassName() }
    public val targetInterfaceName: String get() = targetInterfaceClassName.simpleName

    public val effectAnnotation: KSAnnotationWrapper by lazy { findAnnotation() }

    public val dependencies: Dependencies by lazy { createDependencies() }

    private fun findTargetInterfaceClassDeclaration(): KSClassDeclarationWrapper {
        val interfaces = classDeclaration.interfaces

        if (interfaces.isEmpty()) {
            throw ClassDoesNotImplementInterfaceException(effectAnnotation, classDeclaration)
        }

        return interfaces.singleOrNull()
            ?.let(::KSClassDeclarationWrapper)
            ?: findTargetInterfaceInAnnotation(interfaces)
    }

    private fun findAnnotation() = classDeclaration.wrappedAnnotations
        .first { it.isInstanceOf(annotationClassName) }

    private fun findTargetInterfaceInAnnotation(
        allowedInterfaces: List<KSClassDeclaration>,
    ): KSClassDeclarationWrapper {
        val targetInterface = effectAnnotation.getClassDeclaration(targetArgument)
        if (targetInterface.toClassName() == ANY) throw TargetInterfaceIsNotSpecifiedException(effectAnnotation)
        return if (allowedInterfaces.contains(targetInterface)) {
            KSClassDeclarationWrapper(targetInterface)
        } else {
            throw InvalidTargetInterfaceException(allowedInterfaces, effectAnnotation, classDeclaration)
        }
    }

    private fun createDependencies(): Dependencies {
        val files = listOfNotNull(
            classDeclaration.containingFile,
            targetInterface.containingFile,
        )

        return Dependencies(
            aggregating = false,
            *files.toTypedArray(),
        )
    }

}

public inline fun <reified T> Any.takeIfInstance(): T? {
    return if (this is T) this else null
}

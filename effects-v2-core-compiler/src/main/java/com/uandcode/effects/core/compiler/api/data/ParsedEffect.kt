package com.uandcode.effects.core.compiler.api.data

import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.effects.core.compiler.Const
import com.uandcode.effects.core.compiler.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.core.compiler.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.core.compiler.exceptions.ClassDoesNotImplementInterfaceException
import com.uandcode.effects.core.compiler.exceptions.InvalidTargetInterfaceException

/**
 * Open class that represents a parsed effect. Subclasses can be created
 * in plugins that extend this core compiler.
 */
public open class ParsedEffect(
    public val classDeclaration: KSClassDeclarationWrapper,
    public val annotationClassName: ClassName = EffectClass::class.asClassName(),
    public val targetsArgument: String = Const.TargetArrayArgument,
) : HasDependencies {

    public val className: ClassName = classDeclaration.toClassName()
    public val simpleName: String = className.simpleName

    public val targetInterfaces: List<KSClassDeclarationWrapper> by lazy {
        findTargetInterfaceClassDeclaration()
    }

    public val effectAnnotation: KSAnnotationWrapper by lazy { findAnnotation() }

    override val dependencies: Dependencies by lazy { createDependencies() }

    private fun findTargetInterfaceClassDeclaration(): List<KSClassDeclarationWrapper> {
        val allowedInterfaces = classDeclaration.interfaces

        val targets = effectAnnotation.getClassDeclarationList(Const.TargetArrayArgument)
        val finalTargets = targets.ifEmpty { allowedInterfaces }

        if (finalTargets.isEmpty()) {
            throw ClassDoesNotImplementInterfaceException(effectAnnotation, classDeclaration)
        }

        finalTargets.forEach { finalTarget ->
            if (!allowedInterfaces.contains(finalTarget)) {
                throw InvalidTargetInterfaceException(allowedInterfaces, effectAnnotation, classDeclaration)
            }
        }
        return finalTargets.map(::KSClassDeclarationWrapper)
    }

    private fun findAnnotation() = classDeclaration.wrappedAnnotations
        .first { it.isInstanceOf(annotationClassName) }

    private fun createDependencies(): Dependencies {
        return Dependencies(
            aggregating = false,
            checkNotNull(classDeclaration.containingFile),
        )
    }

}

public inline fun <reified T> Any.takeIfInstance(): T? {
    return if (this is T) this else null
}

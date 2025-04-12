package com.uandcode.effects.compiler.common.api.extensions

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.uandcode.effects.compiler.common.exceptions.InvalidAnnotationArgumentException

/**
 * A wrapper for [KSAnnotation] that provides additional functionality,
 * e.g. resolving the annotation type and getting class declarations from arguments.
 */
public data class KSAnnotationWrapper(
    private val annotation: KSAnnotation,
) : KSAnnotation by annotation {

    private val classDeclarationMap = mutableMapOf<String, KSClassDeclarationWrapper>()
    private val classListDeclarationMap = mutableMapOf<String, List<KSClassDeclarationWrapper>>()

    val resolvedAnnotationType: KSType by lazy {
        annotationType.resolve()
    }

    val className: ClassName get() = resolvedAnnotationType.toClassName()
    val simpleName: String get() = annotation.shortName.asString()
    val canonicalName: String get() = className.canonicalName

    val printableName: String get() = "@$simpleName"

    public fun getClassDeclaration(argumentName: String): KSClassDeclarationWrapper {
        return classDeclarationMap.computeIfAbsent(argumentName) {
            findClassDeclaration(argumentName)
        }
    }

    public fun getClassDeclarationList(argumentName: String): List<KSClassDeclarationWrapper> {
        return classListDeclarationMap.computeIfAbsent(argumentName) {
            findClassDeclarations(argumentName)
        }
    }

    public fun getString(argumentName: String): String {
        val argument = getArgumentByName(argumentName)
        return argument.value.toString()
    }

    @Suppress("UNCHECKED_CAST")
    public fun getStringList(argumentName: String): List<String> {
        val argument = getArgumentByName(argumentName)
        return argument.value as List<String>
    }

    public inline fun <reified T> isInstanceOf(): Boolean {
        return isInstanceOf(T::class.asTypeName())
    }

    public fun isInstanceOf(typeName: TypeName): Boolean {
        return resolvedAnnotationType.toTypeName() == typeName
    }

    private fun KSAnnotationWrapper.findClassDeclaration(argumentName: String): KSClassDeclarationWrapper {
        val argument = getArgumentByName(argumentName)
        val argumentValue = argument.value as? KSType
            ?: throw InvalidAnnotationArgumentException(this)
        return (argumentValue.declaration as? KSClassDeclaration)
            ?.let(::KSClassDeclarationWrapper)
            ?: throw InvalidAnnotationArgumentException(this)
    }

    private fun KSAnnotationWrapper.findClassDeclarations(argumentName: String): List<KSClassDeclarationWrapper> {
        val argument = getArgumentByName(argumentName)
        val argumentValue = argument.value as? List<KSType>
            ?: throw InvalidAnnotationArgumentException(this)
        return argumentValue
            .map { it.declaration }
            .filterIsInstance<KSClassDeclaration>()
            .map(::KSClassDeclarationWrapper)
    }

    private fun getArgumentByName(argumentName: String): KSValueArgument {
        return arguments.first { it.name?.asString() == argumentName }
    }
}

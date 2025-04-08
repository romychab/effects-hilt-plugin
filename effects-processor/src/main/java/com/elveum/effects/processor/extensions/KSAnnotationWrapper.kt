package com.elveum.effects.processor.extensions

import com.elveum.effects.processor.exceptions.InvalidTargetArgumentException
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

data class KSAnnotationWrapper(
    private val annotation: KSAnnotation,
) : KSAnnotation by annotation {

    private val classDeclarationMap = mutableMapOf<String, KSClassDeclarationWrapper>()
    private val classListDeclarationMap = mutableMapOf<String, List<KSClassDeclarationWrapper>>()

    val resolvedAnnotationType: KSType by lazy {
        annotationType.resolve()
    }

    val className: ClassName get() = resolvedAnnotationType.toClassName()
    val simpleName: String get() = annotation.shortName.asString()

    val printableName: String get() = "@$simpleName"

    fun getClassDeclaration(argumentName: String): KSClassDeclarationWrapper {
        return classDeclarationMap.computeIfAbsent(argumentName) {
            findClassDeclaration(argumentName)
        }
    }

    fun getClassDeclarationList(argumentName: String): List<KSClassDeclarationWrapper> {
        return classListDeclarationMap.computeIfAbsent(argumentName) {
            findClassDeclarations(argumentName)
        }
    }

    fun getString(argumentName: String): String {
        val argument = arguments.first { it.name?.asString() == argumentName }
        return argument.value.toString()
    }

    inline fun <reified T> isInstanceOf(): Boolean {
        return isInstanceOf(T::class.asTypeName())
    }

    fun isInstanceOf(typeName: TypeName): Boolean {
        return resolvedAnnotationType.toTypeName() == typeName
    }

    private fun KSAnnotationWrapper.findClassDeclaration(argumentName: String): KSClassDeclarationWrapper {
        val argument = arguments.first { it.name?.asString() == argumentName }
        val argumentValue = argument.value as? KSType
            ?: throw InvalidTargetArgumentException(this)
        return (argumentValue.declaration as? KSClassDeclaration)
            ?.let { KSClassDeclarationWrapper(it) }
            ?: throw InvalidTargetArgumentException(this)
    }

    private fun KSAnnotationWrapper.findClassDeclarations(argumentName: String): List<KSClassDeclarationWrapper> {
        val argument = arguments.first { it.name?.asString() == argumentName }
        val argumentValue = argument.value as? List<KSType>
            ?: throw InvalidTargetArgumentException(this)
        return argumentValue
            .map { it.declaration }
            .filterIsInstance<KSClassDeclaration>()
            .map(::KSClassDeclarationWrapper)
    }
}

inline fun <reified T> Sequence<KSAnnotationWrapper>.firstInstanceOf(): KSAnnotationWrapper {
    return first { it.isInstanceOf<T>() }
}

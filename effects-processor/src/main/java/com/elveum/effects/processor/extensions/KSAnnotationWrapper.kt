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

    private val classDeclarationMap = mutableMapOf<String, KSClassDeclaration>()

    val resolvedAnnotationType: KSType by lazy {
        annotationType.resolve()
    }

    val className: ClassName get() = resolvedAnnotationType.toClassName()
    val simpleName: String get() = annotation.shortName.asString()
    val canonicalName: String get() = className.canonicalName

    val printableName: String get() = "@$simpleName"

    fun getClassDeclaration(argumentName: String): KSClassDeclaration {
        return classDeclarationMap.computeIfAbsent(argumentName) {
            findClassDeclaration(argumentName)
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

    private fun KSAnnotationWrapper.findClassDeclaration(argumentName: String): KSClassDeclaration {
        val argument = arguments.first { it.name?.asString() == argumentName }
        val argumentValue = argument.value as? KSType
            ?: throw InvalidTargetArgumentException(this)
        return argumentValue.declaration as? KSClassDeclaration
            ?: throw InvalidTargetArgumentException(this)
    }

}

inline fun <reified T> Sequence<KSAnnotationWrapper>.firstInstanceOf(): KSAnnotationWrapper {
    return first { it.isInstanceOf<T>() }
}

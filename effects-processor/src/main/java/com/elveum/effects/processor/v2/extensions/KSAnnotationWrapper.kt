package com.elveum.effects.processor.v2.extensions

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
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

    inline fun <reified T> isInstanceOf(): Boolean {
        return resolvedAnnotationType.toTypeName() == T::class.asTypeName()
    }

}

inline fun <reified T> Sequence<KSAnnotationWrapper>.firstInstanceOf(): KSAnnotationWrapper {
    return first { it.isInstanceOf<T>() }
}

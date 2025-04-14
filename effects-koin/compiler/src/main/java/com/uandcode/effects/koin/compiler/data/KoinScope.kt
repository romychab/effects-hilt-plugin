package com.uandcode.effects.koin.compiler.data

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.koin.compiler.Const
import com.uandcode.effects.koin.compiler.exception.ScopeAnnotationConflictException

sealed class KoinScope {

    abstract fun toMetadataString(): String

    data class Class(val className: ClassName) : KoinScope() {
        override fun toMetadataString(): String = "${Const.ClassPrefix}${className.canonicalName}"
    }

    data class Named(val name: String) : KoinScope() {
        override fun toMetadataString(): String = name
    }

    data object Empty : KoinScope() {
        override fun toMetadataString(): String = ""
    }

    companion object {
        fun fromMetadataAnnotation(annotation: KSAnnotationWrapper): KoinScope {
            val value = annotation.getString(Const.MetadataKoinScopeAnnotationArg)
            return if (value.startsWith(Const.ClassPrefix)) {
                val qualifiedName = value.substring(Const.ClassPrefix.length)
                Class(ClassName.bestGuess(qualifiedName))
            } else if (value.isEmpty()) {
                Empty
            } else {
                Named(value)
            }
        }

        fun fromEffectClass(classDeclaration: KSClassDeclarationWrapper): KoinScope {
            val classScope = classDeclaration.wrappedAnnotations
                .firstOrNull { it.isInstanceOf(Const.KoinClassScopeAnnotationName) }
            val namedScope = classDeclaration.wrappedAnnotations
                .firstOrNull { it.isInstanceOf(Const.KoinNamedScopeAnnotationName) }
            return if (classScope != null && namedScope != null) {
                throw ScopeAnnotationConflictException(classDeclaration)
            } else if (classScope != null) {
                Class(classScope.getClassDeclaration(Const.AnnotationValueArg).toClassName())
            } else if (namedScope != null) {
                Named(namedScope.getString(Const.AnnotationValueArg))
            } else {
                Empty
            }
        }
    }
}

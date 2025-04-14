package com.uandcode.effects.koin.compiler.data

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.koin.compiler.Const
import com.uandcode.effects.koin.compiler.exception.ScopeAnnotationConflictException

class KoinParsedEffect(
    classDeclaration: KSClassDeclarationWrapper,
    annotationClassName: ClassName,
) : ParsedEffect(classDeclaration, annotationClassName) {

    val koinScope: KoinScope by lazy { parseKoinScope() }

    private fun parseKoinScope(): KoinScope {
        val classScope = classDeclaration.wrappedAnnotations
            .firstOrNull { it.isInstanceOf(Const.KoinClassScopeAnnotationName) }
        val namedScope = classDeclaration.wrappedAnnotations
            .firstOrNull { it.isInstanceOf(Const.KoinNamedScopeAnnotationName) }
        return if (classScope != null && namedScope != null) {
            throw ScopeAnnotationConflictException(classDeclaration)
        } else if (classScope != null) {
            KoinScope.Class(classScope.getClassDeclaration(Const.AnnotationValueArg).toClassName())
        } else if (namedScope != null) {
            KoinScope.Named(namedScope.getString(Const.AnnotationValueArg))
        } else {
            KoinScope.Empty
        }
    }

}

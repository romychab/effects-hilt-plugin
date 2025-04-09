package com.uandcode.effects.core.compiler.api.extensions

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration

/**
 * Wrapper for [KSClassDeclaration] that provides additional properties.
 * For example, you can easily list all interfaces that the class implements.
 */
public data class KSClassDeclarationWrapper(
    private val classDeclaration: KSClassDeclaration,
) : KSClassDeclaration by classDeclaration {

    val interfaces: List<KSClassDeclarationWrapper> by lazy { findAllInterfaces() }
    val simpleNameText: String get() = simpleName.asString()

    val wrappedAnnotations: Sequence<KSAnnotationWrapper> by lazy {
        annotations.map(::KSAnnotationWrapper)
    }

    private fun findAllInterfaces(): List<KSClassDeclarationWrapper> {
        return superTypes
            .map { typeReference ->
                typeReference.resolve().declaration
            }
            .filterIsInstance<KSClassDeclaration>()
            .filter { classDeclaration ->
                classDeclaration.classKind == ClassKind.INTERFACE
            }
            .toList()
            .map(::KSClassDeclarationWrapper)
    }

}

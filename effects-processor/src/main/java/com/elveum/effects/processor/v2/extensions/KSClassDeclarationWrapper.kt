package com.elveum.effects.processor.v2.extensions

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration

data class KSClassDeclarationWrapper(
    private val classDeclaration: KSClassDeclaration,
) : KSClassDeclaration by classDeclaration {

    val interfaces: List<KSClassDeclaration> by lazy { findAllInterfaces() }
    val simpleNameText: String get() = simpleName.asString()

    val wrappedAnnotations: Sequence<KSAnnotationWrapper> by lazy {
        annotations.map(::KSAnnotationWrapper)
    }

    private fun findAllInterfaces(): List<KSClassDeclaration> {
        return superTypes
            .map { typeReference ->
                typeReference.resolve().declaration
            }
            .filterIsInstance<KSClassDeclaration>()
            .filter { classDeclaration ->
                classDeclaration.classKind == ClassKind.INTERFACE
            }
            .toList()
    }

}
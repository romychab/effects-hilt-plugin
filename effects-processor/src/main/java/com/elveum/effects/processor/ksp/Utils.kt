package com.elveum.effects.processor.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.ksp.toTypeName

fun KSAnnotation.isQualifier(): Boolean {
    val annotationDeclaration = annotationType.resolve().declaration
    return annotationDeclaration.annotations.any {  annotation ->
        annotation.annotationType.toTypeName() == KspNames.qualifier
    }
}

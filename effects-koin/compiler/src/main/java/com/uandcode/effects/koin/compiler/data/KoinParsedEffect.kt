package com.uandcode.effects.koin.compiler.data

import com.squareup.kotlinpoet.ClassName
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper

class KoinParsedEffect(
    classDeclaration: KSClassDeclarationWrapper,
    annotationClassName: ClassName,
) : ParsedEffect(classDeclaration, annotationClassName) {

    val koinScope: KoinScope by lazy { KoinScope.fromEffectClass(classDeclaration) }

}

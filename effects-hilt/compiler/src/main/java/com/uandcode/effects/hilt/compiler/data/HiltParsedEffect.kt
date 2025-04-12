package com.uandcode.effects.hilt.compiler.data

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.hilt.compiler.Const
import com.uandcode.effects.hilt.compiler.HiltComponentClassDeclaration
import com.uandcode.effects.hilt.compiler.exceptions.InvalidHiltComponentException

class HiltParsedEffect(
    classDeclaration: KSClassDeclarationWrapper,
) : ParsedEffect(classDeclaration, Const.HiltEffectAnnotationName) {

    private val hiltComponentDeclaration: HiltComponentClassDeclaration by lazy {
        findHiltComponentClassDeclaration()
    }
    val hiltComponent: SupportedHiltComponent by lazy {
        SupportedHiltComponent.fromClassName(
            hiltComponentDeclaration.toClassName(),
            effectAnnotation,
        )
    }

    private fun findHiltComponentClassDeclaration(): HiltComponentClassDeclaration {
        val classDeclaration = effectAnnotation.getClassDeclaration(Const.InstallInArgument)
        val wrappedClassDeclaration = KSClassDeclarationWrapper(classDeclaration)
        return when {
            wrappedClassDeclaration.toClassName() == ANY -> HiltComponentClassDeclaration.Default
            wrappedClassDeclaration.isHiltComponent() -> HiltComponentClassDeclaration.Declaration(wrappedClassDeclaration)
            else -> throw InvalidHiltComponentException(effectAnnotation)
        }
    }

    private fun KSClassDeclarationWrapper.isHiltComponent(): Boolean {
        return wrappedAnnotations.any {
            it.isInstanceOf(Const.DefineComponentName)
        }
    }

}
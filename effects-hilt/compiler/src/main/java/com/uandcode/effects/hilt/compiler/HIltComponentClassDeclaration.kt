package com.uandcode.effects.hilt.compiler

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper

sealed class HiltComponentClassDeclaration {

    abstract fun toClassName(): ClassName

    data class Declaration(
        val declaration: KSClassDeclarationWrapper,
    ) : HiltComponentClassDeclaration() {
        override fun toClassName(): ClassName {
            return declaration.toClassName()
        }
    }

    data object Default : HiltComponentClassDeclaration() {
        override fun toClassName(): ClassName {
            return Const.ActivityRetainedComponentName
        }
    }

}

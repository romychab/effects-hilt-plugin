package com.elveum.effects.processor.v2.extensions

import com.elveum.effects.processor.v2.InvalidHiltComponentException
import com.elveum.effects.processor.v2.data.Const
import com.elveum.effects.processor.v2.data.takeIfInstance
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

sealed class HiltComponentClassDeclaration {

    abstract fun toClassName(): ClassName
    abstract fun findHiltScope(effectAnnotation: KSAnnotationWrapper): ClassName

    data class Declaration(
        val declaration: KSClassDeclarationWrapper
    ) : HiltComponentClassDeclaration() {

        override fun toClassName(): ClassName {
            return declaration.toClassName()
        }

        override fun findHiltScope(effectAnnotation: KSAnnotationWrapper): ClassName {
            return declaration.wrappedAnnotations.firstOrNull { componentAnnotation ->
                componentAnnotation.resolvedAnnotationType.declaration
                    .takeIfInstance<KSClassDeclaration>()
                    ?.let(::KSClassDeclarationWrapper)
                    ?.run {
                        wrappedAnnotations.any { scopeAnnotation ->
                            scopeAnnotation.isInstanceOf(Const.JavaxScopeAnnotationName)
                                    || scopeAnnotation.isInstanceOf(Const.JakartaScopeAnnotationName)
                        }
                    } ?: false
            }?.className ?: throw InvalidHiltComponentException(effectAnnotation)
        }
    }

    data object Default : HiltComponentClassDeclaration() {
        override fun toClassName(): ClassName {
            return Const.ActivityRetainedComponentName
        }

        override fun findHiltScope(effectAnnotation: KSAnnotationWrapper): ClassName {
            return Const.ActivityRetainedScope
        }
    }

}
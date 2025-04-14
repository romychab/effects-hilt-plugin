package com.uandcode.effects.koin.compiler.data

import com.squareup.kotlinpoet.ClassName
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.koin.compiler.Const

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
        fun parseMetadataAnnotation(annotation: KSAnnotationWrapper): KoinScope {
            val value = annotation.getString(Const.AnnotationValueArg)
            return if (value.startsWith(Const.ClassPrefix)) {
                val qualifiedName = value.substring(Const.ClassPrefix.length)
                Class(ClassName.bestGuess(qualifiedName))
            } else if (value.isEmpty()) {
                Empty
            } else {
                Named(value)
            }
        }
    }
}

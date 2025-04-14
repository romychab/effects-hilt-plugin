package com.uandcode.effects.koin.compiler

import com.squareup.kotlinpoet.ClassName

object Const {
    const val AnnotationValueArg = "value"
    const val ClassPrefix = "__class::"

    val KoinEffectAnnotationName = ClassName("com.uandcode.effects.koin.annotations", "KoinEffect")
    val KoinEffectMetadataAnnotationName = ClassName("com.uandcode.effects.koin.annotations", "KoinEffectMetadata")
    val KoinClassScopeAnnotationName = ClassName("com.uandcode.effects.koin.annotations", "InstallEffectToClassScope")
    val KoinNamedScopeAnnotationName = ClassName("com.uandcode.effects.koin.annotations", "InstallEffectToNamedScope")
    val GeneratedKoinExtensionObjectClassName = ClassName("com.uandcode.effects.koin.kspcontract", "AnnotationBasedKoinEffectExtension")
    val KoinApplicationClassName = ClassName("org.koin.core", "KoinApplication")
}

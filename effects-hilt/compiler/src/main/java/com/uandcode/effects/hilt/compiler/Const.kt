package com.uandcode.effects.hilt.compiler

import com.squareup.kotlinpoet.ClassName

object Const {

    const val InstallInArgument: String = "installIn"
    const val MetadataHiltComponent: String = "hiltComponentClassName"

    val HiltEffectAnnotationName = ClassName("com.uandcode.effects.hilt.annotations", "HiltEffect")
    val HiltEffectMetadataAnnotationName = ClassName("com.uandcode.effects.hilt.annotations", "HiltEffectMetadata")

    val SingletonComponentName = ClassName("dagger.hilt.components", "SingletonComponent")
    val ActivityRetainedComponentName = ClassName("dagger.hilt.android.components", "ActivityRetainedComponent")
    val ViewModelComponentName = ClassName("dagger.hilt.android.components", "ViewModelComponent")
    val ActivityComponentName = ClassName("dagger.hilt.android.components", "ActivityComponent")
    val FragmentComponentName = ClassName("dagger.hilt.android.components", "FragmentComponent")

    val DefineComponentName = ClassName("dagger.hilt", "DefineComponent")
}

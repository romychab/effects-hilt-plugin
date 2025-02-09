@file:Suppress("ConstPropertyName")

package com.elveum.effects.processor.v2.data

import com.elveum.effects.annotations.CustomEffect
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName

object Const {
    val CustomEffectAnnotationName: ClassName = CustomEffect::class.asClassName()
    const val TargetArgument: String = "target"
}

package com.uandcode.effects.hilt.compiler.data

import com.squareup.kotlinpoet.ClassName
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.hilt.compiler.Const
import com.uandcode.effects.hilt.compiler.exceptions.InvalidHiltComponentException

class SupportedHiltComponent(
    val componentName: ClassName,
    val qualifier: String,
) {

    val canonicalName = componentName.canonicalName
    val simpleName = componentName.simpleName

    companion object {
        val all = listOf(
            SupportedHiltComponent(Const.SingletonComponentName, "singleton"),
            SupportedHiltComponent(Const.ActivityRetainedComponentName, "activityRetained"),
            SupportedHiltComponent(Const.ViewModelComponentName, "viewModel"),
            SupportedHiltComponent(Const.ActivityComponentName, "activity"),
            SupportedHiltComponent(Const.FragmentComponentName, "fragment"),
        )

        fun fromClassName(
            className: ClassName,
            effectAnnotation: KSAnnotationWrapper,
        ): SupportedHiltComponent {
            return all.firstOrNull {
                it.componentName == className
            } ?: throw InvalidHiltComponentException(
                effectAnnotation,
            )
        }
    }
}

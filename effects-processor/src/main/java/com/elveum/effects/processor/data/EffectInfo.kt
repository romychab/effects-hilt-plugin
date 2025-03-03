package com.elveum.effects.processor.data

import com.elveum.effects.annotations.HiltEffect
import com.elveum.effects.processor.exceptions.InvalidInstallInArgumentException
import com.elveum.effects.processor.extensions.HiltComponentClassDeclaration
import com.elveum.effects.processor.extensions.KSAnnotationWrapper
import com.elveum.effects.processor.extensions.KSClassDeclarationWrapper
import com.elveum.effects.processor.extensions.firstInstanceOf
import com.elveum.effects.processor.parser.findTargetInterfaceClassDeclaration
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

class EffectInfo(
    originEffectClassDeclaration: KSClassDeclaration
) {

    val effectClassDeclaration: KSClassDeclarationWrapper = KSClassDeclarationWrapper(originEffectClassDeclaration)

    val effectClassName = effectClassDeclaration.toClassName()
    val effectName = effectClassName.simpleName

    val pkg: String get() = effectClassDeclaration.packageName.asString()
    val effectAnnotation: KSAnnotationWrapper by lazy { findCustomAnnotation() }
    val targetInterface: KSClassDeclarationWrapper by lazy { findTargetInterfaceClassDeclaration() }
    val targetInterfaceClassName: ClassName by lazy { targetInterface.toClassName() }
    val targetInterfaceName: String get() = targetInterfaceClassName.simpleName
    val dependencies: Dependencies by lazy { createDependencies() }
    private val hiltComponentDeclaration: HiltComponentClassDeclaration by lazy {
        findHiltComponentClassDeclaration()
    }
    val hiltComponent: ClassName by lazy { hiltComponentDeclaration.toClassName() }
    val hiltScope: ClassName by lazy { hiltComponentDeclaration.findHiltScope(effectAnnotation) }

    val cleanUpMethodName: EffectCleanUpMethodName by lazy {
        EffectCleanUpMethodName(effectAnnotation.getString(Const.CleanUpMethodNameArgument))
    }

    private fun findCustomAnnotation() = effectClassDeclaration.wrappedAnnotations
        .firstInstanceOf<HiltEffect>()

    private fun findHiltComponentClassDeclaration(): HiltComponentClassDeclaration {
        val classDeclaration = effectAnnotation.getClassDeclaration(Const.InstallInArgument)
        val wrappedClassDeclaration = KSClassDeclarationWrapper(classDeclaration)
        return when {
            wrappedClassDeclaration.toClassName() == ANY -> HiltComponentClassDeclaration.Default
            wrappedClassDeclaration.isHiltComponent() -> HiltComponentClassDeclaration.Declaration(wrappedClassDeclaration)
            else -> throw InvalidInstallInArgumentException(effectAnnotation)
        }
    }

    private fun KSClassDeclarationWrapper.isHiltComponent(): Boolean {
        return wrappedAnnotations.any {
            it.isInstanceOf(Const.DefineComponentName)
        }
    }

    private fun createDependencies(): Dependencies {
        val files = listOfNotNull(
            effectClassDeclaration.containingFile,
            targetInterface.containingFile,
        )

        return Dependencies(
            aggregating = false,
            *files.toTypedArray(),
        )
    }

}

inline fun <reified T> Any.takeIfInstance(): T? {
    return if (this is T) this else null
}
package com.elveum.effects.processor.v2.data

import com.elveum.effects.annotations.CustomEffect
import com.elveum.effects.processor.v2.InvalidInstallInArgumentException
import com.elveum.effects.processor.v2.extensions.HiltComponentClassDeclaration
import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper
import com.elveum.effects.processor.v2.extensions.KSClassDeclarationWrapper
import com.elveum.effects.processor.v2.extensions.firstInstanceOf
import com.elveum.effects.processor.v2.parser.findTargetInterfaceClassDeclaration
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

    val pkg: String get() = effectClassDeclaration.packageName.asString()
    val effectAnnotation: KSAnnotationWrapper by lazy { findCustomAnnotation() }
    val targetInterface: KSClassDeclarationWrapper by lazy { findTargetInterfaceClassDeclaration() }
    val targetInterfaceName: String get() = targetInterface.simpleName.asString()
    val dependencies: Dependencies by lazy { createDependencies() }

    private val hiltComponentDeclaration: HiltComponentClassDeclaration by lazy {
        findHiltComponentClassDeclaration()
    }
    val hiltComponent: ClassName by lazy { hiltComponentDeclaration.toClassName() }
    val hiltScope: ClassName by lazy { hiltComponentDeclaration.findHiltScope(effectAnnotation) }

    private fun findCustomAnnotation(): KSAnnotationWrapper {
        return effectClassDeclaration.wrappedAnnotations
            .firstInstanceOf<CustomEffect>()
    }

    private fun findHiltComponentClassDeclaration(): HiltComponentClassDeclaration {
        val classDeclaration = effectAnnotation.getClassDeclaration(Const.InstallInArgument)
        val wrappedClassDeclaration = KSClassDeclarationWrapper(classDeclaration)

        val isHiltComponent = wrappedClassDeclaration.wrappedAnnotations.any {
            it.isInstanceOf(Const.DefineComponentName)
        }
        if (!isHiltComponent) {
            throw InvalidInstallInArgumentException(effectAnnotation)
        }

        return if (wrappedClassDeclaration.toClassName() == ANY) {
            return HiltComponentClassDeclaration.Default
        } else {
            HiltComponentClassDeclaration.Declaration(wrappedClassDeclaration)
        }
    }

    private fun createDependencies(): Dependencies {
        val implementationFile = effectClassDeclaration.containingFile
        val interfaceFile = targetInterface.containingFile
        checkNotNull(implementationFile)
        checkNotNull(interfaceFile)
        return Dependencies(
            aggregating = false,
            implementationFile,
            interfaceFile,
        )
    }

}

inline fun <reified T> Any.takeIfInstance(): T? {
    return if (this is T) this else null
}
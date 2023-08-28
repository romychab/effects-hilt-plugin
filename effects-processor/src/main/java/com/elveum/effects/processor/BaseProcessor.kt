package com.elveum.effects.processor

import com.elveum.effects.annotations.SideEffect
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

abstract class BaseProcessor : AbstractProcessor() {

    protected val messenger: Messager
        get() = processingEnv.messager

    protected val elements: Elements
        get() = processingEnv.elementUtils

    protected val filer: Filer
        get() = processingEnv.filer

    protected val types: Types
        get() = processingEnv.typeUtils

    override fun process(p0: MutableSet<out TypeElement>, environment: RoundEnvironment): Boolean {
        try {
            environment.getElementsAnnotatedWith(SideEffect::class.java)
                .forEach {
                    generateFor(it as TypeElement)
                }
        } catch (e: ElementException) {
            raiseError(e.message ?: "", e.element)
        } catch (e: Exception) {
            this.messenger.printMessage(Diagnostic.Kind.ERROR, "Got an error: " + e.message)
        }
        return true
    }

    protected abstract fun generateFor(element: TypeElement)

    private fun raiseError(message: String, element: Element) {
        messenger.printMessage(
            Diagnostic.Kind.ERROR,
            message + ": See '" + element.simpleName + "' in " + element.enclosingElement,
            element
        )
    }

}
package com.elveum.effects.processor

import com.squareup.javapoet.ClassName
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

data class Qualifier(
    val pkg: String,
    val name: String
) {
    fun toJClassName(): ClassName {
        return ClassName.get(pkg, name)
    }
}

class ParsedElements(
    val qualifier: Qualifier?,
    val origin: TypeElement,
    val directInterface: TypeElement,
    val pkg: String,
    val methods: Set<ExecutableElement>,
    val originName: String,
    val suggestedMediatorName: String,
    val otherInterfaces: List<TypeElement>
)
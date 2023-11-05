package com.elveum.effects.processor.ksp.generators

import com.elveum.effects.processor.ksp.KspParsedElements
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

class KspClassWriter(
    private val codeGenerator: CodeGenerator,
    private val parsedElements: KspParsedElements,
) {

    fun write(name: String, typeSpec: TypeSpec) {
        val file = FileSpec.builder(parsedElements.pkg, name)
            .addType(typeSpec)
            .build()

        val dependencies = listOfNotNull(
            parsedElements.origin.containingFile,
            parsedElements.directInterface.containingFile,
        )
        file.writeTo(
            codeGenerator = codeGenerator,
            Dependencies(
                aggregating = false,
                *dependencies.toTypedArray(),
            )
        )
    }
}

package com.elveum.effects.processor.v2.generators.base

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

class KspClassV2Writer(
    private val codeGenerator: CodeGenerator,
) {

    fun write(typeSpec: TypeSpec, dependencies: Dependencies, pkg: String) {
        val className = typeSpec.name
        checkNotNull(className)
        val file = FileSpec.builder(pkg, className)
            .addType(typeSpec)
            .build()

        file.writeTo(
            codeGenerator = codeGenerator,
            dependencies = dependencies,
        )
    }

    fun write(templateBasedClassContent: TemplateBasedClassContent) = with(templateBasedClassContent) {
        val outputStream = codeGenerator.createNewFile(
            dependencies = dependencies,
            packageName = pkg,
            fileName = className,
        )
        outputStream.writer().use {
            it.write(content)
        }
    }

}

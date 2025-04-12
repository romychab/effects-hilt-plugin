package com.uandcode.effects.compiler.common

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.TemplateBasedClassContent

internal class KspClassWriterImpl(
    private val codeGenerator: CodeGenerator,
) : KspClassWriter {

    override fun write(
        typeSpec: TypeSpec,
        dependencies: Dependencies,
        pkg: String,
        builder: FileSpec.Builder.() -> Unit,
    ) {
        val className = checkNotNull(typeSpec.name)
        val file = FileSpec.builder(pkg, className)
            .addType(typeSpec)
            .apply(builder)
            .build()

        file.writeTo(
            codeGenerator = codeGenerator,
            dependencies = dependencies,
        )
    }

    override fun write(templateBasedClassContent: TemplateBasedClassContent) = with(templateBasedClassContent) {
        val outputStream = codeGenerator.createNewFile(
            dependencies = dependencies,
            packageName = pkg,
            fileName = className,
        )
        outputStream.writer().use {
            it.write(templateBasedClassContent.buildContent())
        }
    }

}

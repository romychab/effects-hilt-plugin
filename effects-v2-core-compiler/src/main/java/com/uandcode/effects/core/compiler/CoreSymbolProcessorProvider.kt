package com.uandcode.effects.core.compiler

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.uandcode.effects.core.compiler.api.ProcessingMode

public class CoreSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return CoreSymbolProcessor(
            logger = environment.logger,
            processingMode = ProcessingMode.fromOptions(environment.options),
            codeGenerator = environment.codeGenerator,
        )
    }

}

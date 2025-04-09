package com.uandcode.effects.core.compiler.api

public enum class ProcessingMode {

    // should be used in library modules:
    GenerateMetadata,

    // should be used in application modules:
    AggregateMetadata;

    internal companion object {

        private const val PROCESSING_MODE_ARG = "effects.processor.metadata"

        fun fromOptions(options: Map<String, String>): ProcessingMode {
            return when (options[PROCESSING_MODE_ARG]) {
                "generate" -> GenerateMetadata
                "aggregate" -> AggregateMetadata
                else -> AggregateMetadata // default
            }
        }

    }

}

package com.uandcode.effects.compiler.common.api

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.uandcode.effects.compiler.common.api.data.GeneratedProxy
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.api.data.ParsedMetadata
import com.uandcode.effects.compiler.common.api.data.GroupedMetadata
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper

/**
 * An interface that defines the contract for an effect extension.
 *
 * Plugins that extend the core compiler must implement this interface.
 *
 * Usage example:
 *
 * ```
 * // 1. Create a pure Kotlin library module
 *
 * // 2. Add the dependency from this compiler-common module
 *
 * // 3. Implement the interface, let's say:
 * class MyEffectExtension : EffectExtension {
 *     // the implementation is here
 * }
 *
 * // 4. Create a SymbolProcessorProvider:
 * class MySymbolProcessorProvider : SymbolProcessorProvider {
 *     override fun create(
 *         environment: SymbolProcessorEnvironment
 *     ): SymbolProcessor {
 *         // 5. Return an instance of EffectSymbolProcessor
 *         return EffectSymbolProcessor(
 *             environment = environment,
 *             // 6. Pass the instance of your effect extension
 *             effectExtension = MyEffectExtension(),
 *         )
 *     }
 * }
 *
 * // 7. Register your SymbolProcessorProvider in the META-INF/services resource dir.
 * //    The file name must be 'com.google.devtools.ksp.processing.SymbolProcessorProvider'.
 * //    The content of the file must be the fully qualified name of your provider class.
 * ```
 */
public interface EffectExtension {

    /**
     * Define an annotation that marks effect classes. The annotation must be
     * an equivalent of `@EffectClass` annotation.
     *
     * The annotation class must contain at least 1 parameter:
     * - `val targets: Array<KClass<*>> = []`
     *
     * Optionally, you can add other parameters to the annotation.
     */
    public val effectAnnotation: ClassName

    /**
     * Define an annotation that marks auto-generated metadata classes. The annotation must be
     * an equivalent of `@EffectMetadata` annotation.
     *
     * The annotation class must contain at least 2 parameters:
     * - `val interfaceClassNames: Array<String>`
     * - `val implementationClassName: String`
     *
     * Optionally, you can add other parameters to the annotation.
     */
    public val metadataAnnotation: ClassName

    /**
     * Parse the effect class marked with the [effectAnnotation] annotation.
     */
    public fun parseEffect(
        classDeclaration: KSClassDeclarationWrapper,
    ): ParsedEffect

    /**
     * Get metadata from the parsed effect.
     */
    public fun buildMetadataFromParsedEffect(
        parsedEffect: ParsedEffect,
    ): ParsedMetadata

    /**
     * The returned generator will be used to generate metadata classes
     * based on [ParsedEffect] instances created by [parseEffect] method.
     *
     * @see AbstractMetadataGenerator
     */
    public fun metadataGenerator(writer: KspClassWriter): AbstractMetadataGenerator

    /**
     * Parse metadata from the annotation defined by [metadataAnnotation]
     * and generated by the generator from [metadataGenerator] method.
     */
    public fun buildMetadataFromAnnotation(
        interfaceDeclarations: List<KSClassDeclarationWrapper>,
        implementationClassDeclaration: KSClassDeclarationWrapper,
        metadataDeclaration: KSClassDeclaration,
        metadataAnnotation: KSAnnotationWrapper,
    ): ParsedMetadata

    /**
     * Validate all parsed effects if needed.
     *
     * @throws AbstractEffectKspException
     */
    public fun validateEffects(parsedEffects: Sequence<ParsedEffect>)

    /**
     * Validate all parsed metadata if needed.
     *
     * @throws AbstractEffectKspException
     */
    public fun validateMetadata(
        groupedMetadata: Map<ClassName, List<ParsedMetadata>>,
    )

    /**
     * Override this method for generating additional classes within
     * your extension.
     */
    public fun generateExtensions(
        groupedMetadata: GroupedMetadata,
        generatedProxies: List<GeneratedProxy>,
        writer: KspClassWriter,
    )

}

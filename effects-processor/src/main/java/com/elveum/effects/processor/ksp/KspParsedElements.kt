package com.elveum.effects.processor.ksp

import com.elveum.effects.processor.Qualifier
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

data class KspParsedElements(
    val qualifier: Qualifier?,
    val origin: KSClassDeclaration, // origin annotated class
    val directInterface: KSClassDeclaration, // target interface
    val pkg: String, // package of the origin class
    val methods: List<KSFunctionDeclaration>,
    val originName: String,
    val suggestedMediatorName: String,
    val otherInterfaces: List<KSClassDeclaration>
)

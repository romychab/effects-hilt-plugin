package com.elveum.effects.processor.v2.generators

import com.elveum.effects.processor.v2.data.EffectInfo
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger

class EffectMediatorGenerator(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) {

    fun generate(effectInfo: EffectInfo, writer: KspClassV2Writer): Result {
        //val mediatorName = "${effectInfo.targetInterfaceName}Mediator"
        // todo
        return Result()
    }

    class Result
}
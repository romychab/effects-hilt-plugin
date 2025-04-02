package com.uandcode.effects.stub.api

public class InvalidEffectSetupException : Exception(
    "The library is not configured properly:\n" +
    "1. Review your dependencies in build.gradle file; make sure KSP and your DI framework is configured correctly\n" +
    "2. Do not forget to replace RootEffectComponents in integration tests by using setEmpty() and/or setGlobal() method"
)

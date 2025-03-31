package com.uandcode.effects.core.exceptions

import com.uandcode.effects.core.exceptions.AnnotationNames.effectApplication

public class InvalidEffectSetupException : Exception(
    "$effectApplication annotation or the library is not configured properly.\n" +
    "1. Make sure you've added $effectApplication annotation to your main application module\n" +
    "2. Review your dependencies in build.gradle file; make sure KSP and your DI framework is added correctly\n" +
    "3. Do not forget to replace RootEffectComponents in integration tests by using setEmpty() and/or setGlobal() method"
)

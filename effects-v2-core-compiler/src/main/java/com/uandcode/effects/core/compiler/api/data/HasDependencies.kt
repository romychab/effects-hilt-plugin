package com.uandcode.effects.core.compiler.api.data

import com.google.devtools.ksp.processing.Dependencies

public interface HasDependencies {
    public val dependencies: Dependencies
}
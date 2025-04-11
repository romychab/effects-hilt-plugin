package com.uandcode.effects.compiler.common.api.data

import com.google.devtools.ksp.processing.Dependencies

public interface HasDependencies {
    public val dependencies: Dependencies
}
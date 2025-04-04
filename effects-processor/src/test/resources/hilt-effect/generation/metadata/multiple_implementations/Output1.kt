package hilt_effects_plugin

import com.elveum.effects.annotations.TargetInterfaceMetadata

@TargetInterfaceMetadata(
    implementationClassName = "test.TestClass1",
    interfaceClassName = "test.TestInterface",
    hiltComponentClassName = "dagger.hilt.android.components.ActivityRetainedComponent",
    hiltScopeClassName = "dagger.hilt.android.scopes.ActivityRetainedScoped",
    cleanUpMethodName = "",
)
public class __test_TestClass1_Metadata

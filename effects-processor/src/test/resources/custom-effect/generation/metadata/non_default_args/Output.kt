package hilt_effects_plugin

import com.elveum.effects.annotations.TargetInterfaceMetadata

@TargetInterfaceMetadata(
    implementationClassName = "test.TestClass",
    interfaceClassName = "test.TestInterface",
    hiltComponentClassName = "dagger.hilt.android.components.FragmentComponent",
    hiltScopeClassName = "dagger.hilt.android.scopes.FragmentScoped",
)
public class __test_TestClass_Metadata

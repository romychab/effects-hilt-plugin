%PACKAGE_STATEMENT%

import com.elveum.effects.annotations.TargetInterfaceMetadata

@TargetInterfaceMetadata(
    implementationClassName = "%EFFECT_IMPL_CLASSNAME%",
    interfaceClassName = "%TARGET_INTERFACE_CLASSNAME%",
    hiltComponentClassName = "%HILT_COMPONENT_CLASSNAME%",
    hiltScopeClassName = "%HILT_SCOPE_CLASSNAME%",
)
public class %CLASSNAME%

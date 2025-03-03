%PACKAGE_STATEMENT%

import com.elveum.effects.annotations.TargetInterfaceMetadata

@TargetInterfaceMetadata(
    implementationClassName = "%EFFECT_IMPL_CLASSNAME%",
    interfaceClassName = "%TARGET_INTERFACE_CLASSNAME%",
    hiltComponentClassName = "%HILT_COMPONENT_CLASSNAME%",
    hiltScopeClassName = "%HILT_SCOPE_CLASSNAME%",
    cleanUpMethodName = "%CLEAN_UP_METHOD_NAME%",
)
public class %CLASSNAME%

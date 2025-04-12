%PACKAGE_STATEMENT%

import com.uandcode.effects.hilt.annotations.HiltEffectMetadata

@HiltEffectMetadata(
    interfaceClassNames = %EFFECT_INTERFACE_CLASSNAMES%,
    implementationClassName = "%EFFECT_IMPL_CLASSNAME%",
    hiltComponentClassName = "%HILT_COMPONENT_CLASSNAME%",
)
public class %CLASSNAME%

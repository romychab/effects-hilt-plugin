%PACKAGE_STATEMENT%

import com.uandcode.effects.koin.annotations.KoinEffectMetadata

@KoinEffectMetadata(
    interfaceClassNames = %EFFECT_INTERFACE_CLASSNAMES%,
    implementationClassName = "%EFFECT_IMPL_CLASSNAME%",
    koinScope = "%EFFECT_KOIN_SCOPE%"
)
public class %CLASSNAME%

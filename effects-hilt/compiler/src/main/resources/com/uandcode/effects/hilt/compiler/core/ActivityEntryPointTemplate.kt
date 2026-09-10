%PACKAGE_STATEMENT%

import com.uandcode.effects.hilt.ActivityEffectEntryPoint
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
public interface %CLASSNAME% : ActivityEffectEntryPoint

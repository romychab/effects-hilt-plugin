%PACKAGE_STATEMENT%

import com.uandcode.effects.hilt.FragmentEffectEntryPoint
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@EntryPoint
@InstallIn(FragmentComponent::class)
public interface %CLASSNAME% : FragmentEffectEntryPoint

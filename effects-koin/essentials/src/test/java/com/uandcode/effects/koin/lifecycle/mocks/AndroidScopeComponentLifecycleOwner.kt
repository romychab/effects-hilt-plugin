package com.uandcode.effects.koin.lifecycle.mocks

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.testing.lifecycle.TestLifecycleOwner
import io.mockk.mockk
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.Koin
import org.koin.core.scope.Scope

class AndroidScopeComponentLifecycleOwner(
    val testLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner(),
    override val scope: Scope = mockk(),
) : LifecycleOwner, AndroidScopeComponent {
    override val lifecycle: Lifecycle get() = testLifecycleOwner
}

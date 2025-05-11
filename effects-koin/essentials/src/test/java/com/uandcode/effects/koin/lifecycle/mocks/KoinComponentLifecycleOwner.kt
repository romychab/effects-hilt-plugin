package com.uandcode.effects.koin.lifecycle.mocks

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.testing.lifecycle.TestLifecycleOwner
import io.mockk.mockk
import org.koin.core.Koin
import org.koin.core.component.KoinComponent

class KoinComponentLifecycleOwner(
    val testLifecycleOwner: TestLifecycleOwner = TestLifecycleOwner(),
    val koinInstance: Koin = mockk()
) : LifecycleOwner, KoinComponent {
    override val lifecycle: Lifecycle get() = testLifecycleOwner
    override fun getKoin(): Koin = koinInstance
}

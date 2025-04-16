package com.uandcode.effects.core.compose.testing

import com.uandcode.effects.core.testing.lifecycle.TestLifecycleOwner

public interface TestComposition {

    public val lifecycleOwner: TestLifecycleOwner

    public fun assertCompositionExists(name: String)

    public fun findChildComposition(name: String): TestComposition?

    public fun emitFrame()

}

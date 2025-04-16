package com.uandcode.effects.core.compose.testing.internal

import androidx.compose.runtime.BroadcastFrameClock
import com.uandcode.effects.core.compose.testing.TestComposition
import com.uandcode.effects.core.testing.lifecycle.TestLifecycleOwner
import com.uandcode.flowtest.FlowTestScope
import org.junit.Assert.assertNotNull

internal class TestCompositionImpl(
    override val lifecycleOwner: TestLifecycleOwner,
    private val frameClock: BroadcastFrameClock,
    private val scope: FlowTestScope,
    private val rootNode: Node,
) : TestComposition {

    override fun assertCompositionExists(name: String) {
        assertNotNull(
            "A composition with name '$name' does not exist",
            rootNode.find(name)
        )
    }

    override fun findChildComposition(name: String): TestComposition? {
        return rootNode.find(name)?.let {
            TestCompositionImpl(lifecycleOwner, frameClock, scope, it)
        }
    }

    override fun emitFrame() {
        frameClock.sendFrame(0)
        scope.runCurrent()
    }

}

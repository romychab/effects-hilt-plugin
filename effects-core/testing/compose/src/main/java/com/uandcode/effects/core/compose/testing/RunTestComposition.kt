@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalComposeApi::class)

package com.uandcode.effects.core.compose.testing

import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.uandcode.effects.core.compose.testing.internal.TestCompositionImpl
import com.uandcode.effects.core.compose.testing.internal.Node
import com.uandcode.effects.core.compose.testing.internal.TestNodeApplier
import com.uandcode.effects.core.testing.lifecycle.TestLifecycleOwner
import com.uandcode.flowtest.FlowTestScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Start a test composition.
 *
 * Can be useful is unit tests, as test composition does not require
 * android dependencies or Robolectric. The test composition can use
 * [TestElement] and [TestGroup] as composable nodes. CompositionLocals and
 * Compose Effects can be used too.
 *
 * After changing any state value, you must call [TestComposition.emitFrame]
 * to trigger recomposition.
 *
 * You can control the lifecycle by using [TestComposition.lifecycleOwner].
 */
public fun FlowTestScope.setContent(
    content: @Composable () -> Unit
): TestComposition {
    setupSnapshotSystem()
    val frameClock = BroadcastFrameClock()
    val composer = runComposer(frameClock)
    val lifecycleOwner = TestLifecycleOwner()
    val rootNode = GroupNode()
    createComposition(rootNode, composer, lifecycleOwner, content)
    return TestCompositionImpl(lifecycleOwner, frameClock, this, rootNode)
}

private fun FlowTestScope.runComposer(frameClock: MonotonicFrameClock): Recomposer {
    val dispatcher = UnconfinedTestDispatcher(scope.testScheduler)
    val composer = Recomposer(dispatcher)
    executeInBackground(dispatcher + frameClock) {
        composer.runRecomposeAndApplyChanges()
    }
    return composer
}

private fun FlowTestScope.setupSnapshotSystem() {
    executeInBackground {
        val handle = Snapshot.registerGlobalWriteObserver {
            Snapshot.sendApplyNotifications()
        }
        try {
            awaitCancellation()
        } finally {
            handle.dispose()
        }
    }
}

private fun createComposition(
    rootNode: GroupNode,
    composer: Recomposer,
    lifecycleOwner: TestLifecycleOwner,
    content: @Composable () -> Unit,
) {
    val applier = TestNodeApplier(rootNode)
    Composition(applier, composer).apply {
        setContent {
            CompositionLocalProvider(
                LocalLifecycleOwner provides lifecycleOwner
            ) {
                content()
            }
        }
    }
}
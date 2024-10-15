package com.elveum.effects.core

import kotlinx.coroutines.CancellationException

/**
 * Internal exception which indicates that activity has been stopped
 * and that's why suspend side-effect methods have to be temporarily
 * cancelled on the activity side.
 */
internal class ResourceScopeCancelledException : CancellationException()
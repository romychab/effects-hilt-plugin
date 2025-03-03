package dagger.hilt.android.components

import dagger.hilt.DefineComponent
import dagger.hilt.android.scopes.FragmentScoped

/**
 * HACK: it is impossible to add Hilt as a dependency to this module, because
 * Hilt is an Android library, but our module is a pure Kotlin module.
 * So let's copy Hilt annotations like this:
 */
@FragmentScoped
@DefineComponent
annotation class FragmentComponent

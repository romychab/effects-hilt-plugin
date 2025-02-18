package dagger.hilt.components

import dagger.hilt.DefineComponent
import javax.inject.Singleton

/**
 * HACK: it's impossible to add Hilt as a dependency to this module, because
 * Hilt is an Android library, but our module is a pure Kotlin module.
 * So let's copy Hilt annotations like this:
 */
@Singleton
@DefineComponent
annotation class SingletonComponent

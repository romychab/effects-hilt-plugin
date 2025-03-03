package dagger.hilt.android

/**
 * HACK: it is impossible to add Hilt as a dependency to this module, because
 * Hilt is an Android library, but our module is a pure Kotlin module.
 * So let's copy Hilt annotations like this:
 */
annotation class HiltAndroidApp

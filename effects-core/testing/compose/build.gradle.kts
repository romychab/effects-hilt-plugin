plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.compose)
}

dependencies {
    api(projects.effectsCore.testing.lifecycle)

    implementation(libs.junit)
    implementation(libs.compose.runtime)
    implementation(libs.lifecycle.runtime.compose)

    api(libs.coroutines.test)
    api(libs.flowtest)
}

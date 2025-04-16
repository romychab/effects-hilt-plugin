plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
    alias(libs.plugins.compose)
}

publishConfig {
    artifactId = "effects2-core-compose"
    description = "Effects Core Library - Extensions for Jetpack Compose."
}

dependencies {
    testImplementation(projects.effectsCore.testing.compose)
    testImplementation(projects.effectsCore.testing.mocks)
    api(projects.effectsCore.lifecycle)

    implementation(libs.lifecycle.runtime.compose)
    api(libs.immutable.collections)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

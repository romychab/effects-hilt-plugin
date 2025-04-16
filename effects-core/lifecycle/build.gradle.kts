plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    artifactId = "effects2-core-lifecycle"
    description = "Effects Core Library - Extensions for LifecycleOwner"
}

dependencies {
    api(projects.effectsCore.essentials)
    api(projects.effectsCore.annotations)
    testImplementation(projects.effectsCore.testing.mocks)
    testImplementation(projects.effectsCore.testing.lifecycle)

    api(libs.lifecycle.jvm)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.flowtest)
}

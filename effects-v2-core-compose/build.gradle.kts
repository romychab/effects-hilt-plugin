plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
    alias(libs.plugins.compose)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-core-compose"
    description = "A library (core Jetpack Compose extension) for simplifying the implementation of one-off events"
}

dependencies {
    api(projects.effectsV2CoreLifecycle)

    implementation(libs.lifecycle.runtime.compose)
    api(libs.immutable.collections)
}

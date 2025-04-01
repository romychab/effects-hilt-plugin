plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-core-lifecycle"
    description = "A library (extension for androidx-lifecycle) for simplifying the implementation of one-off events"
}

dependencies {
    api(libs.androidx.lifecycle.jvm)
    api(projects.effectsV2Core)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

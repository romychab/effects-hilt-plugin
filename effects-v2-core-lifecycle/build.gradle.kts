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
    api(projects.effectsV2Core)
    api(projects.effectsV2CoreAnnotations)
    testImplementation(projects.effectsV2CoreTestmocks)

    api(libs.androidx.lifecycle.jvm)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.flowtest)
}

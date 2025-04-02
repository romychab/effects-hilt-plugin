plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-core"
    description = "A library (core artifact) for simplifying the implementation of one-off events"
}

dependencies {
    api(projects.effectsV2StubApi)
    compileOnly(projects.effectsV2Stub)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.flowtest)
    testImplementation(projects.effectsV2Stub)
}

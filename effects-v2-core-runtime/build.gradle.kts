plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-core-runtime"
    description = "A library for easier replacing effect components at runtime (e.g. in integration tests)"
}

dependencies {
    api(projects.effectsV2Core)
    testImplementation(projects.effectsV2CoreTestmocks)

    implementation(libs.byte.buddy)
    implementation(libs.coroutines.core)
    implementation(buildSrcLibs.kotlinx.reflection)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.flowtest)
}

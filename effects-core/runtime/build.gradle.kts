plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-core-runtime"
    description = "Effects Core Library - Runtime version and an alternative to compile-time KSP annotation processor."
}

dependencies {
    api(projects.effectsCore.essentials)
    testImplementation(projects.effectsCore.testmocks)

    implementation(libs.coroutines.core)
    implementation(buildSrcLibs.kotlinx.reflection)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.flowtest)
}

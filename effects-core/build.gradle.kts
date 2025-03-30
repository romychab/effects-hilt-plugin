plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.maven.publish)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.elveum.effects.core"
}

publishConfig {
    groupId = "com.elveum"
    artifactId = "effects-core"
    description = "Hilt plugin (core library) for simplifying the implementation of one-off events"
}

dependencies {
    api(projects.effectsAnnotations)

    implementation(libs.coroutines.core)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.flowtest)
}

plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-core-compiler"
    description = "A library (core annotation processor) for simplifying the implementation of one-off events"
}

dependencies {
    implementation(projects.effectsV2Core)
    implementation(projects.effectsV2CoreAnnotations)

    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
    implementation(libs.ksp.api)
}

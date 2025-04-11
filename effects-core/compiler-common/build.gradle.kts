plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    artifactId = "effects2-core-compiler-common"
    description = "Effects Core Library - Reusable common classes for implementing KSP annotation processors."
}

dependencies {
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
    implementation(libs.ksp.api)
}

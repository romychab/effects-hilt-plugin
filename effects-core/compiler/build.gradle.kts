import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    artifactId = "effects2-core-compiler"
    description = "Effects Core Library - KSP annotation processor."
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    implementation(projects.effectsCore.compilerCommon)
    implementation(projects.effectsCore.annotations)

    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
    implementation(libs.ksp.api)

    testImplementation(libs.junit)
    testImplementation(libs.ksp.testing)
}

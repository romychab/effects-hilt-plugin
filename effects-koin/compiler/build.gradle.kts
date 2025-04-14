import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    artifactId = "effects2-koin-compiler"
    description = "Effects Koin Plugin (KSP annotation processor) for simplifying the implementation of one-off events"
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    implementation(projects.effectsCore.compilerCommon)
    implementation(projects.effectsKoin.annotations)

    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
    implementation(libs.ksp.api)
}

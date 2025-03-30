import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.elveum"
    artifactId = "effects-processor"
    description = "Hilt plugin (annotation processor) for simplifying the implementation of one-off events"
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    implementation(projects.effectsAnnotations)

    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
    implementation(libs.ksp.api)

    testImplementation(libs.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.ksp.testing)
    testImplementation(libs.javax.inject)
}

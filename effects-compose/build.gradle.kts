plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.maven.publish)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose)
}

android {
    namespace = "com.elveum.effects.compose"
    buildFeatures {
        compose = true
    }
}

publishConfig {
    groupId = "com.elveum"
    artifactId = "effects-compose"
    description = "Hilt plugin (Jetpack Compose library) for simplifying the implementation of one-off events"
}

dependencies {
    api(projects.effectsCore)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}

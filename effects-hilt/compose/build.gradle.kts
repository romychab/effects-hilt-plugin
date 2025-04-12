plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.maven.publish)
    alias(libs.plugins.compose)
}

android {
    namespace = "com.uandcode.effects.hilt.compose"
    buildFeatures {
        compose = true
    }
}

publishConfig {
    artifactId = "effects2-hilt-compose"
    description = "Effects Hilt Plugin - Jetpack Compose extension for simplifying the implementation of one-off events"
}

dependencies {
    api(projects.effectsHilt.essentials)
    implementation(projects.effectsCore.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.androidx.activity)
    api(libs.immutable.collections)
}

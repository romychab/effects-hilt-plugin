import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.elveum.effects.example.features.details"
    buildFeatures {
        compose = true
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    implementation(projects.appExamples.app2Multimodule.composeComponents)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.container)
    implementation(libs.coroutines.core)
    implementation(libs.coil.compose)
    implementation(libs.hilt.navigation.compose)
}

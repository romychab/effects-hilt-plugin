import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.uandcode.example.hilt.multimodule.features.list"
    buildFeatures {
        compose = true
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    api(projects.appExamples.hilt.appMultimodule.effectInterfaces)
    implementation(projects.appExamples.hilt.appMultimodule.composeComponents)

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

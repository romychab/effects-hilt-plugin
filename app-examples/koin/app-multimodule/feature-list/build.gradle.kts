import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.compose)
}

android {
    namespace = "com.uandcode.example.koin.multimodule.features.list"
    buildFeatures {
        compose = true
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    api(projects.appExamples.koin.appMultimodule.effectInterfaces)
    implementation(projects.appExamples.koin.appMultimodule.composeComponents)

    implementation(libs.koin.android.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.container)
    implementation(libs.coroutines.core)
    implementation(libs.coil.compose)
    implementation(libs.lifecycle.viewmodel.compose)
}

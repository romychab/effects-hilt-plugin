import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.compose)
}

android {
    namespace = "com.uandcode.example.koin.multimodule.compose"
    buildFeatures {
        compose = true
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    implementation(libs.container)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}

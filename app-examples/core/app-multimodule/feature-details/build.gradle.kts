import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.compose)
}

android {
    namespace = "com.uandcode.example.core.multimodule.features.details"
    buildFeatures {
        compose = true
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    implementation(projects.appExamples.core.appMultimodule.composeComponents)
    implementation(projects.appExamples.core.appMultimodule.effectInterfaces)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.container)
    implementation(libs.coroutines.core)
    implementation(libs.coil.compose)
    implementation(libs.lifecycle.viewmodel.compose)
}

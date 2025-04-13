import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.compose)
}

android {
    namespace = "com.uandcode.example.core.multimodule.features.list"
    buildFeatures {
        compose = true
    }
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled
}

dependencies {
    api(projects.appExamples.core.appMultimodule.effectInterfaces)
    implementation(projects.appExamples.core.appMultimodule.composeComponents)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.container)
    implementation(libs.coroutines.core)
    implementation(libs.coil.compose)
    implementation(libs.lifecycle.viewmodel.compose)
}

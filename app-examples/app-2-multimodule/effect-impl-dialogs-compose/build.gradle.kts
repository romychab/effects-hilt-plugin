plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.elveum.effects.example.dialogs.compose"
    buildFeatures {
        compose = true
    }
}

ksp {
    arg("effects.processor.metadata", "generate")
}

dependencies {
    api(projects.appExamples.app2Multimodule.effectInterfaces)

    implementation("com.elveum:effects-core:1.0.3")
    ksp("com.elveum:effects-processor:1.0.3")

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}

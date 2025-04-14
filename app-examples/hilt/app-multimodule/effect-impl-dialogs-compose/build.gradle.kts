plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.uandcode.example.hilt.multimodule.effects.dialogs.compose"
    buildFeatures {
        compose = true
    }
}

ksp {
    arg("effects.processor.metadata", "generate")
}

dependencies {
    api(projects.appExamples.hilt.appMultimodule.effectInterfaces)

    implementation(projects.effectsHilt.annotations)
    ksp(projects.effectsHilt.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}

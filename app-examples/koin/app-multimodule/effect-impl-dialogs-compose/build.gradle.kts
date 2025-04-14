plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.example.koin.multimodule.effects.dialogs.compose"
    buildFeatures {
        compose = true
    }
}

ksp {
    arg("effects.processor.metadata", "generate")
}

dependencies {
    api(projects.appExamples.koin.appMultimodule.effectInterfaces)

    implementation(projects.effectsKoin.annotations)
    ksp(projects.effectsKoin.compiler)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}

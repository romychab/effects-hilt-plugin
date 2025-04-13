plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.example.core.multimodule.effects.dialogs.compose"
    buildFeatures {
        compose = true
    }
}

ksp {
    arg("effects.processor.metadata", "generate")
}

dependencies {
    api(projects.appExamples.core.appMultimodule.effectInterfaces)

    implementation(projects.effectsCore.essentials)
    implementation(projects.effectsCore.annotations)
    ksp(projects.effectsCore.compiler)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}

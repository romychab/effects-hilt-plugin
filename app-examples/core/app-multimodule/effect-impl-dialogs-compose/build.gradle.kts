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

    implementation(libs.effects.core.essentials)
    implementation(libs.effects.core.annotations)
    ksp(libs.effects.core.compiler)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}

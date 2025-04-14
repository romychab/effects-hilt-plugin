plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.uandcode.example.hilt.multimodule.effects.dialogs.android"
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
}

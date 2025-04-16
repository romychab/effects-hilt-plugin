plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.uandcode.example.hilt.multimodule.effects.toasts"
}

ksp {
    arg("effects.processor.metadata", "generate")
}

dependencies {
    api(projects.appExamples.hilt.appMultimodule.effectInterfaces)

    implementation(libs.effects.hilt.annotations)
    ksp(libs.effects.hilt.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}

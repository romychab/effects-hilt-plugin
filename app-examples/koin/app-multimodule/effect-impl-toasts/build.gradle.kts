plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.example.koin.multimodule.effects.toasts"
}

ksp {
    arg("effects.processor.metadata", "generate")
}

dependencies {
    api(projects.appExamples.koin.appMultimodule.effectInterfaces)

    implementation(projects.effectsKoin.annotations)
    ksp(projects.effectsKoin.compiler)
}

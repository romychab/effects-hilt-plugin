plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.example.koin.multimodule.effects.dialogs.android"
}

ksp {
    arg("effects.processor.metadata", "generate")
}

dependencies {
    api(projects.appExamples.koin.appMultimodule.effectInterfaces)

    implementation(libs.effects.koin.annotations)
    ksp(libs.effects.koin.compiler)

    implementation(libs.coroutines.core)
}

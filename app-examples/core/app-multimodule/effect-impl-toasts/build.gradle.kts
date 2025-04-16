plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.example.core.multimodule.effects.toasts"
}

ksp {
    arg("effects.processor.metadata", "generate")
}

dependencies {
    api(projects.appExamples.core.appMultimodule.effectInterfaces)

    implementation(libs.effects.core.essentials)
    implementation(libs.effects.core.annotations)
    ksp(libs.effects.core.compiler)
}

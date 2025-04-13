plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.example.core.multimodule.effects.dialogs.android"
}

ksp {
    arg("effects.processor.metadata", "generate")
}

dependencies {
    api(projects.appExamples.core.appMultimodule.effectInterfaces)

    implementation(projects.effectsCore.essentials)
    implementation(projects.effectsCore.annotations)
    ksp(projects.effectsCore.compiler)
}

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.effects.hilt.compiler.test"
}

dependencies {
    implementation(projects.effectsHilt.compiler)
    implementation(projects.effectsHilt.essentials)
    testImplementation(projects.effectsCore.testing.ksp)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.ksp.api)
    testImplementation(libs.coroutines.core)
}

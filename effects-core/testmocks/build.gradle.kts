plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.effectsCore.essentials)
    implementation(projects.effectsCore.annotations)
    ksp(projects.effectsCore.compiler)
}

plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(projects.effectsV2Core)
    implementation(projects.effectsV2CoreAnnotations)
    ksp(projects.effectsV2CoreCompiler)
}

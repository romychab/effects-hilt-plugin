plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.maven.publish)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.effects.hilt"
}

publishConfig {
    artifactId = "effects2-hilt"
    description = "Effects Hilt Plugin for simplifying the implementation of one-off events."
}

dependencies {
    api(projects.effectsCore.essentials)
    api(projects.effectsHilt.annotations)
    implementation(projects.effectsCore.lifecycle)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}

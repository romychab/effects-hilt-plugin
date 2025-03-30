plugins {
    alias(libs.plugins.custom.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.elveum.effects.example"
    defaultConfig {
        applicationId = "com.elveum.effects.example"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(projects.effectsCompose)
    ksp(projects.effectsProcessor)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.activity)
    implementation(libs.navigation.compose)
    implementation(libs.java.faker)
    implementation(libs.coil.compose)
    implementation(libs.container)
    implementation(libs.coroutines.core)
    implementation(libs.kotlin.serialization.core)
    implementation(libs.hilt.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.leak.canary)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}